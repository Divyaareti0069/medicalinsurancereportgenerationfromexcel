package com.example.medicalinsurancereportgenerationfromexcel.Service;

import com.example.medicalinsurancereportgenerationfromexcel.Model.Invoice;
import com.example.medicalinsurancereportgenerationfromexcel.Repository.InvoiceRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository repository;

    @Autowired
    private MongoTemplate template;

    public List<Invoice> processExcel(MultipartFile file,String providerName) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            List<Invoice> invoices = new ArrayList<>();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Invoice invoice = new Invoice();

                invoice.setPolicy(getStringValue(row.getCell(0)));
                String plan=getStringValue(row.getCell(1));

                invoice.setCustomerDefinedSort(getStringValue(row.getCell(2)));
                invoice.setSubscriberName(getStringValue(row.getCell(3)));
                String id = getStringValue(row.getCell(5));
                String coverageDates = getStringValue(row.getCell(4));
                invoice.setId(new Invoice.InvoiceId(id, coverageDates,plan));
                invoice.setStatus(getStringValue(row.getCell(6)));
                invoice.setVolume(getStringValue(row.getCell(7)));
                invoice.setChargeAmount(getNumericValue(row.getCell(8)));

                invoice.setAdjCode(getStringValue(row.getCell(9)));
                invoice.setCoverageType(getStringValue(row.getCell(10)));
                invoice.setBenefitGroup1(getStringValue(row.getCell(11)));
                invoice.setBenefitGroup2(getStringValue(row.getCell(12)));
                invoice.setBenefitGroup3(getStringValue(row.getCell(13)));
                invoice.setProviderName(providerName);

                invoices.add(invoice);
            }

            List<Invoice> savedInvoices = repository.saveAll(invoices);
            workbook.close();
            return savedInvoices;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private String getStringValue(Cell cell) {
        if (cell != null) {
            if (cell.getCellType() == CellType.NUMERIC) {
                return NumberToTextConverter.toText(cell.getNumericCellValue());
            } else {
                return cell.getStringCellValue();
            }
        }
        return null;
    }

    private Float getNumericValue(Cell cell) {
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return (float) cell.getNumericCellValue();
        }
        return 0.0f;
    }
}