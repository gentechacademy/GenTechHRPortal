package com.gentech.hrportal.service;

import com.gentech.hrportal.entity.EmployeeProfile;
import com.gentech.hrportal.entity.SalarySlip;
import com.gentech.hrportal.entity.User;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGenerationService {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${app.company.name:GenTech Solutions}")
    private String companyName;
    
    @Value("${app.company.address:}")
    private String companyAddress;
    
    @Autowired
    private EmployeeProfileService employeeProfileService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
    private static final String[] MONTH_NAMES = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };
    private static final Color HEADER_COLOR = new Color(51, 102, 153);
    private static final Color SUBHEADER_COLOR = new Color(76, 140, 200);
    private static final Color TABLE_HEADER_COLOR = new Color(230, 240, 250);
    private static final Color TOTAL_ROW_COLOR = new Color(240, 248, 255);
    
    /**
     * Generate a professional PDF salary slip
     * @param salarySlip the salary slip entity
     * @return the file path of the generated PDF
     */
    public String generateSalarySlipPdf(SalarySlip salarySlip) {
        try {
            // Create directories if they don't exist
            String salarySlipsDir = uploadDir + "/salary-slips";
            Path dirPath = Paths.get(salarySlipsDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // Generate unique filename
            String filename = String.format("salary-slip-%d-%d-%d-%d.pdf", 
                salarySlip.getEmployee().getId(), 
                salarySlip.getYear(), 
                salarySlip.getMonth(),
                System.currentTimeMillis());
            String filePath = salarySlipsDir + "/" + filename;
            
            // Create document
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Add content to PDF
            addCompanyHeader(document);
            addEmployeeDetails(document, salarySlip);
            addEarningsTable(document, salarySlip);
            addDeductionsTable(document, salarySlip);
            addNetPaySection(document, salarySlip);
            addFooter(document);
            
            document.close();
            writer.close();
            
            // Update salary slip with PDF path and generation time
            salarySlip.setPdfUrl(filePath);
            
            return filePath;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate salary slip PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get PDF file as byte array for download
     * @param salarySlipId the salary slip ID
     * @return byte array of the PDF file
     */
    public byte[] getPdfBytes(Long salarySlipId) {
        try {
            String salarySlipsDir = uploadDir + "/salary-slips";
            File dir = new File(salarySlipsDir);
            
            if (!dir.exists() || !dir.isDirectory()) {
                throw new RuntimeException("Salary slips directory not found");
            }
            
            // Find the PDF file for this salary slip ID
            File[] files = dir.listFiles((d, name) -> name.startsWith("salary-slip-" + salarySlipId + "-"));
            
            if (files == null || files.length == 0) {
                throw new RuntimeException("PDF not found for salary slip ID: " + salarySlipId);
            }
            
            // Return the most recent file (in case there are multiple)
            File pdfFile = files[0];
            for (File file : files) {
                if (file.lastModified() > pdfFile.lastModified()) {
                    pdfFile = file;
                }
            }
            
            return Files.readAllBytes(pdfFile.toPath());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to read PDF file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get PDF bytes by file path
     * @param filePath the path to the PDF file
     * @return byte array of the PDF file
     */
    public byte[] getPdfBytesByPath(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new RuntimeException("PDF file not found: " + filePath);
            }
            return Files.readAllBytes(path);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read PDF file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get month name from month number
     * @param month month number (1-12)
     * @return month name
     */
    private String getMonthName(int month) {
        if (month >= 1 && month <= 12) {
            return MONTH_NAMES[month - 1];
        }
        return "Unknown";
    }
    
    private void addCompanyHeader(Document document) throws DocumentException {
        // Company Name
        Font companyFont = new Font(Font.HELVETICA, 20, Font.BOLD, HEADER_COLOR);
        Paragraph companyPara = new Paragraph(companyName, companyFont);
        companyPara.setAlignment(Element.ALIGN_CENTER);
        document.add(companyPara);
        
        // Company Address (if available)
        if (companyAddress != null && !companyAddress.isEmpty()) {
            Font addressFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY);
            Paragraph addressPara = new Paragraph(companyAddress, addressFont);
            addressPara.setAlignment(Element.ALIGN_CENTER);
            document.add(addressPara);
        }
        
        // Payslip Title
        Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLACK);
        Paragraph titlePara = new Paragraph("SALARY SLIP", titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingBefore(10);
        document.add(titlePara);
        
        // Separator Line
        document.add(Chunk.NEWLINE);
    }
    
    private void addEmployeeDetails(Document document, SalarySlip salarySlip) throws DocumentException {
        User employee = salarySlip.getEmployee();
        EmployeeProfile profile = employeeProfileService.getProfileByUserId(employee.getId());
        
        Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.DARK_GRAY);
        Font valueFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, SUBHEADER_COLOR);
        
        // Section Header
        Paragraph sectionHeader = new Paragraph("EMPLOYEE DETAILS", headerFont);
        sectionHeader.setSpacingAfter(8);
        document.add(sectionHeader);
        
        // Create table for employee details
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 2f, 1.2f, 2f});
        
        // Row 1
        addDetailCell(table, "Employee Name:", labelFont);
        addValueCell(table, employee.getFullName(), valueFont);
        addDetailCell(table, "Employee ID:", labelFont);
        addValueCell(table, String.valueOf(employee.getId()), valueFont);
        
        // Row 2
        addDetailCell(table, "Department:", labelFont);
        addValueCell(table, profile != null ? profile.getDepartment() : "N/A", valueFont);
        addDetailCell(table, "Designation:", labelFont);
        addValueCell(table, profile != null ? profile.getDesignation() : "N/A", valueFont);
        
        // Row 3 - Payslip Period
        addDetailCell(table, "Payslip For:", labelFont);
        PdfPCell payslipCell = new PdfPCell(new Phrase(
            getMonthName(salarySlip.getMonth()) + " " + salarySlip.getYear(), 
            new Font(Font.HELVETICA, 10, Font.BOLD, HEADER_COLOR)));
        payslipCell.setBorder(Rectangle.NO_BORDER);
        payslipCell.setPadding(4);
        table.addCell(payslipCell);
        addDetailCell(table, "Generated On:", labelFont);
        addValueCell(table, LocalDateTime.now().format(DATE_FORMATTER), valueFont);
        
        document.add(table);
        document.add(Chunk.NEWLINE);
    }
    
    private void addEarningsTable(Document document, SalarySlip salarySlip) throws DocumentException {
        Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, SUBHEADER_COLOR);
        Font labelFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font totalFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
        
        // Section Header
        Paragraph sectionHeader = new Paragraph("EARNINGS", headerFont);
        sectionHeader.setSpacingAfter(8);
        document.add(sectionHeader);
        
        // Create table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1.5f});
        
        // Table Header
        addTableHeader(table, "Description", "Amount (Rs.)");
        
        // Earnings rows
        addAmountRow(table, "Basic Salary", salarySlip.getBasicSalary(), labelFont);
        addAmountRow(table, "House Rent Allowance (HRA)", salarySlip.getHra(), labelFont);
        addAmountRow(table, "Dearness Allowance (DA)", salarySlip.getDa(), labelFont);
        addAmountRow(table, "Special Allowance", salarySlip.getSpecialAllowance(), labelFont);
        addAmountRow(table, "Conveyance", salarySlip.getConveyance(), labelFont);
        addAmountRow(table, "Medical Allowance", salarySlip.getMedical(), labelFont);
        addAmountRow(table, "Other Allowances", salarySlip.getOtherAllowances(), labelFont);
        
        // Bonus (if applicable)
        if (salarySlip.getBonus() != null && salarySlip.getBonus() > 0) {
            Font bonusFont = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(0, 128, 0));
            addAmountRow(table, "Bonus", salarySlip.getBonus(), bonusFont);
        }
        
        // Gross Salary (Total)
        addTotalRow(table, "GROSS SALARY", salarySlip.getGrossSalary(), totalFont);
        
        document.add(table);
        document.add(Chunk.NEWLINE);
    }
    
    private void addDeductionsTable(Document document, SalarySlip salarySlip) throws DocumentException {
        Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, SUBHEADER_COLOR);
        Font labelFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font totalFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
        
        // Section Header
        Paragraph sectionHeader = new Paragraph("DEDUCTIONS", headerFont);
        sectionHeader.setSpacingAfter(8);
        document.add(sectionHeader);
        
        // Create table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1.5f});
        
        // Table Header
        addTableHeader(table, "Description", "Amount (Rs.)");
        
        // Deductions rows
        addAmountRow(table, "Provident Fund (PF)", salarySlip.getPf(), labelFont);
        addAmountRow(table, "Professional Tax", salarySlip.getProfessionalTax(), labelFont);
        addAmountRow(table, "Income Tax (TDS)", salarySlip.getIncomeTax(), labelFont);
        addAmountRow(table, "Other Deductions", salarySlip.getOtherDeductions(), labelFont);
        
        // Total Deductions
        addTotalRow(table, "TOTAL DEDUCTIONS", salarySlip.getTotalDeductions(), totalFont);
        
        document.add(table);
        document.add(Chunk.NEWLINE);
    }
    
    private void addNetPaySection(Document document, SalarySlip salarySlip) throws DocumentException {
        Font netPayLabelFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        Font netPayValueFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.WHITE);
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1.5f});
        
        // Net Pay Row with background color
        PdfPCell labelCell = new PdfPCell(new Phrase("NET PAY", netPayLabelFont));
        labelCell.setBackgroundColor(new Color(0, 100, 0));
        labelCell.setPadding(10);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        String netPayText = String.format("Rs. %,.2f", salarySlip.getNetSalary());
        PdfPCell valueCell = new PdfPCell(new Phrase(netPayText, netPayValueFont));
        valueCell.setBackgroundColor(new Color(0, 100, 0));
        valueCell.setPadding(10);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
        
        document.add(table);
        
        // Amount in words
        Font wordsFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.DARK_GRAY);
        Paragraph wordsPara = new Paragraph("Amount in words: " + convertNumberToWords(salarySlip.getNetSalary()), wordsFont);
        wordsPara.setAlignment(Element.ALIGN_RIGHT);
        wordsPara.setSpacingBefore(5);
        document.add(wordsPara);
        
        document.add(Chunk.NEWLINE);
    }
    
    private void addFooter(Document document) throws DocumentException {
        Font footerFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY);
        
        Paragraph separator = new Paragraph("________________________________________");
        separator.setAlignment(Element.ALIGN_CENTER);
        separator.setSpacingBefore(20);
        document.add(separator);
        
        Paragraph footerPara = new Paragraph(
            "This is a computer-generated document and does not require signature." +
            "\nFor any queries regarding your salary, please contact the HR department.", 
            footerFont);
        footerPara.setAlignment(Element.ALIGN_CENTER);
        footerPara.setSpacingBefore(10);
        document.add(footerPara);
        
        Paragraph generatedPara = new Paragraph(
            "Generated by " + companyName + " HR Portal on " + LocalDateTime.now().format(DATE_FORMATTER), 
            footerFont);
        generatedPara.setAlignment(Element.ALIGN_CENTER);
        generatedPara.setSpacingBefore(10);
        document.add(generatedPara);
    }
    
    // Helper methods
    private void addDetailCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        table.addCell(cell);
    }
    
    private void addValueCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        table.addCell(cell);
    }
    
    private void addTableHeader(PdfPTable table, String col1, String col2) {
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        
        PdfPCell cell1 = new PdfPCell(new Phrase(col1, headerFont));
        cell1.setBackgroundColor(HEADER_COLOR);
        cell1.setPadding(8);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        
        PdfPCell cell2 = new PdfPCell(new Phrase(col2, headerFont));
        cell2.setBackgroundColor(HEADER_COLOR);
        cell2.setPadding(8);
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        table.addCell(cell1);
        table.addCell(cell2);
    }
    
    private void addAmountRow(PdfPTable table, String description, Double amount, Font font) {
        PdfPCell descCell = new PdfPCell(new Phrase(description, font));
        descCell.setPadding(6);
        descCell.setBorderColor(Color.LIGHT_GRAY);
        
        String amountText = amount != null ? String.format("%,.2f", amount) : "0.00";
        PdfPCell amountCell = new PdfPCell(new Phrase(amountText, font));
        amountCell.setPadding(6);
        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        amountCell.setBorderColor(Color.LIGHT_GRAY);
        
        table.addCell(descCell);
        table.addCell(amountCell);
    }
    
    private void addTotalRow(PdfPTable table, String description, Double amount, Font font) {
        PdfPCell descCell = new PdfPCell(new Phrase(description, font));
        descCell.setBackgroundColor(TOTAL_ROW_COLOR);
        descCell.setPadding(8);
        descCell.setBorderColor(HEADER_COLOR);
        descCell.setBorderWidthTop(1.5f);
        
        String amountText = amount != null ? String.format("%,.2f", amount) : "0.00";
        PdfPCell amountCell = new PdfPCell(new Phrase(amountText, font));
        amountCell.setBackgroundColor(TOTAL_ROW_COLOR);
        amountCell.setPadding(8);
        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        amountCell.setBorderColor(HEADER_COLOR);
        amountCell.setBorderWidthTop(1.5f);
        
        table.addCell(descCell);
        table.addCell(amountCell);
    }
    
    /**
     * Convert number to words (Indian format)
     */
    private String convertNumberToWords(Double amount) {
        if (amount == null) return "Zero Rupees Only";
        
        long number = amount.longValue();
        long paise = Math.round((amount - number) * 100);
        
        StringBuilder words = new StringBuilder();
        
        if (number == 0 && paise == 0) {
            return "Zero Rupees Only";
        }
        
        if (number > 0) {
            words.append(convertToWords(number)).append(" Rupees");
        }
        
        if (paise > 0) {
            if (number > 0) {
                words.append(" and ");
            }
            words.append(convertToWords(paise)).append(" Paise");
        }
        
        words.append(" Only");
        return words.toString();
    }
    
    private String convertToWords(long number) {
        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        String[] teens = {"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", 
                         "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        
        if (number == 0) return "";
        
        if (number < 10) return units[(int) number];
        if (number < 20) return teens[(int) (number - 10)];
        if (number < 100) return tens[(int) (number / 10)] + (number % 10 != 0 ? " " + units[(int) (number % 10)] : "");
        if (number < 1000) return units[(int) (number / 100)] + " Hundred" + (number % 100 != 0 ? " " + convertToWords(number % 100) : "");
        if (number < 100000) return convertToWords(number / 1000) + " Thousand" + (number % 1000 != 0 ? " " + convertToWords(number % 1000) : "");
        if (number < 10000000) return convertToWords(number / 100000) + " Lakh" + (number % 100000 != 0 ? " " + convertToWords(number % 100000) : "");
        return convertToWords(number / 10000000) + " Crore" + (number % 10000000 != 0 ? " " + convertToWords(number % 10000000) : "");
    }
}
