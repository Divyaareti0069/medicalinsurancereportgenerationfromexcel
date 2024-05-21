package com.example.medicalinsurancereportgenerationfromexcel.Service;

import com.example.medicalinsurancereportgenerationfromexcel.Exception.DuplicateInvoiceHeaderException;
import com.example.medicalinsurancereportgenerationfromexcel.Model.Invoice;
import com.example.medicalinsurancereportgenerationfromexcel.Model.InvoiceHeader;
import com.example.medicalinsurancereportgenerationfromexcel.Repository.InvoiceHeaderRepository;
import com.example.medicalinsurancereportgenerationfromexcel.Repository.InvoiceRepository;
import com.mongodb.DuplicateKeyException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceHeaderRepository invoiceHeaderRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Pair<List<Invoice>, List<InvoiceHeader>> getInvoicesWithHeaders() {
        List<Invoice> invoices = invoiceRepository.findAll();
        List<InvoiceHeader> invoiceHeaders = invoiceHeaderRepository.findAll();
        return Pair.of(invoices, invoiceHeaders);
    }

    public List<Invoice> processExcel(MultipartFile file, String providerName) {
        List<Invoice> invoices = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                Row firstRow = rowIterator.next();
                InvoiceHeader header = new InvoiceHeader();
                InvoiceHeader.InvoiceHeaderId headerId = new InvoiceHeader.InvoiceHeaderId();
                headerId.setInvoiceDate(getStringValue(firstRow.getCell(1)));
                headerId.setInvoiceNumber(getStringValue(firstRow.getCell(3)));
                headerId.setProviderName(providerName);

                header.setId(headerId);
                header.setRecordCount((int) firstRow.getCell(5).getNumericCellValue());
                header.setUploaded_timeStamp(LocalDateTime.now());

                try {
                    header = invoiceHeaderRepository.save(header);
                } catch (DuplicateKeyException e) {
                    throw new DuplicateInvoiceHeaderException("Duplicate Invoice Header: " + e.getMessage());
                }
            }


            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Invoice invoice = new Invoice();

                invoice.setPolicy(getStringValue(row.getCell(0)));
                String plan = getStringValue(row.getCell(1));

                invoice.setCustomerDefinedSort(getStringValue(row.getCell(2)));
                invoice.setSubscriberName(getStringValue(row.getCell(3)));
                String id = getStringValue(row.getCell(5));
                String coverageDates = getStringValue(row.getCell(4));
                invoice.setId(new Invoice.InvoiceId(id, coverageDates, plan));
                invoice.setStatus(getStringValue(row.getCell(6)));
                invoice.setVolume(getStringValue(row.getCell(7)));
                invoice.setChargeAmount(getNumericValue(row.getCell(8)));

                invoice.setAdjCode(getStringValue(row.getCell(9)));
                invoice.setCoverageType(getStringValue(row.getCell(10)));
                invoice.setBenefitGroup1(getStringValue(row.getCell(11)));
                invoice.setBenefitGroup2(getStringValue(row.getCell(12)));
                invoice.setBenefitGroup3(getStringValue(row.getCell(13)));
                invoice.setProviderName(providerName);
                String[] dates = coverageDates.split("-");
                String startDate = dates[0]; // "01/01/2024"
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate date = LocalDate.parse(startDate, formatter);
                invoice.setYear(date.getYear());
                invoice.setMonth(date.getMonthValue());

                invoices.add(invoice);
            }

            invoices = invoiceRepository.saveAll(invoices);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        return invoices;
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
    public List<Invoice> filterByYear(int year) {
        Query query = new Query();
        query.addCriteria(Criteria.where("year").is(year));
        return mongoTemplate.find(query, Invoice.class);
    }


    public List<Invoice> filterByYearAndMonth(int year, int month) {
        Query query = new Query();
        query.addCriteria(Criteria.where("year").is(year).and("month").is(month));
        return mongoTemplate.find(query, Invoice.class);
    }


    public List<Invoice> filterByMonth(int month) {
        Query query = new Query();
        query.addCriteria(Criteria.where("month").is(month));
        return mongoTemplate.find(query, Invoice.class);
    }

   /* public List<Invoice> filterByYear(int year) {
        String regexPattern = String.format(".*\\d{2}/\\d{2}/%04d.*", year);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id.coverageDates").regex(regexPattern));
        return mongoTemplate.find(query, Invoice.class);
    }

    public List<Invoice> filterByYearAndMonth(int year, int month) {
        String regexPattern = String.format(".*%02d/\\d{2}/%04d.*", month, year);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id.coverageDates").regex(regexPattern));
        return mongoTemplate.find(query, Invoice.class);
    }

    public List<Invoice> filterByMonth(int month) {
        String regexPattern = String.format(".*%02d/\\d{2}/\\d{4}.*", month);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id.coverageDates").regex(regexPattern));
        return mongoTemplate.find(query, Invoice.class);
    }*/
}
