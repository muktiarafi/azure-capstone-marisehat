package dev.muktiarafi.marisehat.service.impl;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.List;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import dev.muktiarafi.marisehat.dto.LabResultDto;
import dev.muktiarafi.marisehat.entity.LabResult;
import dev.muktiarafi.marisehat.entity.Patient;
import dev.muktiarafi.marisehat.enums.Gender;
import dev.muktiarafi.marisehat.exception.ResourceNotFoundException;
import dev.muktiarafi.marisehat.mapper.LabResultMapper;
import dev.muktiarafi.marisehat.repository.LabResultRepository;
import dev.muktiarafi.marisehat.repository.PatientRepository;
import dev.muktiarafi.marisehat.service.LabResultService;
import dev.muktiarafi.marisehat.utils.StorageUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LabResultServiceImpl implements LabResultService {
    private final StorageUtils storageUtils;
    private final PatientRepository patientRepository;
    private final LabResultRepository labResultRepository;
    private final LabResultMapper labResultMapper;

    @Override
    @Transactional
    public LabResult create(UUID patientId, LabResultDto labResultDto) {
        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        var labResultId = UUID.randomUUID();
        var blobName = String.format("resutl-%s.pdf", labResultId);
        var labResult = labResultMapper.labResultDtoToLabResult(labResultDto, blobName, patient);
        labResult = labResultRepository.save(labResult);

        var labResultBytes = generateLabResult(patient, labResult);

        var metadata = Map.of("patientId", patientId.toString());
        var blobSasUrl = uploadToBlobStorage(blobName, metadata, labResultBytes);
        labResult.setBlobUrl(blobSasUrl);

        return labResult;
    }

    @Override
    public java.util.List<LabResult> getPatientLabResult(UUID patientId) {
        var labResults = labResultRepository.findByPatientId(patientId);
        for (var labResult : labResults) {
            var blobClient = storageUtils.getBlobClient(labResult.getBlobName());
            var key = storageUtils.generateUserDelegationKey();
            var blobSasUrl = storageUtils.generateSasToken(blobClient, key);
            labResult.setBlobUrl(blobSasUrl);
        }

        return labResults;
    }

    private byte[] generateLabResult(Patient patient, LabResult labResult) {
        var document = new Document(PageSize.A4);
        var bytes = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, bytes);

        document.open();

        var fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontHeader.setSize(24);
        fontHeader.setStyle(Font.BOLD);

        var header = new Paragraph("LABORATORIUM HEMATOLOGI KLINIK MARISEHAT", fontHeader);
        header.setAlignment(Paragraph.ALIGN_CENTER);
        var headerLineBreak = new Chunk("\n");
        var lineSeparator = new LineSeparator(1, 100, Color.BLACK, Paragraph.ALIGN_CENTER, 1);

        var fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        fontTitle.setStyle(Font.BOLD);

        var title = new Paragraph("Lembar Hasil Pemeriksaan Laboratorium", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);

        var labResultDetailTable = new Table(5);
        labResultDetailTable.setBorder(Rectangle.NO_BORDER);
        labResultDetailTable.setPadding(2);

        var formatter = new SimpleDateFormat("dd-MM-yyyy");
        addLabResultDetailTableRow("Id pemeriksaan", labResult.getId().toString(), labResultDetailTable);
        addLabResultDetailTableRow("Tanggal Pemeriksaan", formatter.format(labResult.getConductedAt()), labResultDetailTable);
        addLabResultDetailTableRow("Nama", patient.getFullName(), labResultDetailTable);
        addLabResultDetailTableRow("No. Telp", patient.getPhoneNumber(), labResultDetailTable);
        addLabResultDetailTableRow("Umur", patient.getPhoneNumber(), labResultDetailTable);

        var gender = patient.getGender() == Gender.MALE ? "Laki-laki" : "Peremepuan" ;
        addLabResultDetailTableRow("Jenis Kelamin", gender, labResultDetailTable);

        var resultTable = new Table(2);
        resultTable.setPadding(4);

        addResultTableRow("Hemogoblin", labResult.getHemogoblin(), resultTable);
        addResultTableRow("Entrosit", labResult.getEntrosit(), resultTable);
        addResultTableRow("Leukosit", labResult.getLeukosit(), resultTable);
        addResultTableRow("Trombosit", labResult.getTrombosit(), resultTable);
        addResultTableRow("Hematokrit", labResult.getHematokrit(), resultTable);

        var noteSection = new Chunk("Catatan");
        List notes = new List();
        for (String note : labResult.getNotes()) {
            notes.add(note);
        }

        document.add(header);
        document.add(headerLineBreak);
        document.add(lineSeparator);
        document.add(title);
        document.add(labResultDetailTable);
        document.add(resultTable);
        document.add(noteSection);
        document.add(notes);

        document.close();

        return bytes.toByteArray();
    }

    private void addLabResultDetailTableRow(String name, String value, Table table) {
        var nameCell = new Cell(name);
        nameCell.setColspan(2);
        nameCell.setBorder(Rectangle.NO_BORDER);

        var semiColon = new Cell(":");
        semiColon.setColspan(1);
        semiColon.setBorder(Rectangle.NO_BORDER);

        var valueCell = new Cell(value);
        valueCell.setColspan(2);
        valueCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(nameCell);
        table.addCell(semiColon);
        table.addCell(valueCell);
    }

    private void addResultTableRow(String name, double value, Table table) {
        var nameCell = new Cell(name);

        Cell valueCell;
        if (value == 0) {
            valueCell = new Cell("Tidak dilakukan pemeriksaan");
            valueCell.setHorizontalAlignment(HorizontalAlignment.CENTER);
        } else {
            valueCell = new Cell(String.valueOf(value));
            valueCell.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        }

        table.addCell(nameCell);
        table.addCell(valueCell);
    }

    private String uploadToBlobStorage(String blobName, Map<String, String> metadata, byte[] bytes) {
        var blobClient = storageUtils.getBlobClient(blobName);
        blobClient.upload(BinaryData.fromBytes(bytes));
        blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(MediaType.APPLICATION_PDF_VALUE));
        blobClient.setMetadata(metadata);

        var delegationKey = storageUtils.generateUserDelegationKey();

        return storageUtils.generateSasToken(blobClient, delegationKey);
    }
}
