package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.MetodoPago;
import com.proyecto.fragataGiratoria.service.MetodoPagoService;

// === Importaciones para PDF (iText 5) ===
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
// =======================================

// === Importaciones para Excel (Apache POI) ===
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.util.IOUtils;
// =============================================

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
// ✅ RUTA BASE FINAL: Usa 'de' para coincidir con la URL de acceso
@RequestMapping("/crud/metodosdepago") 
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    public MetodoPagoController(MetodoPagoService metodoPagoService) {
        this.metodoPagoService = metodoPagoService;
    }

    // ------------------------------------------------------------------
    // --- 1. LEER (READ) - Listar Métodos de Pago ---
    // ------------------------------------------------------------------
    @GetMapping // Mapea a: /crud/metodosdepago
    public String mostrarMetodosPago(Model model) {
        List<MetodoPago> metodos = metodoPagoService.listarMetodosPago();
        model.addAttribute("metodosPago", metodos);
        model.addAttribute("titulo", "Gestión de Métodos de Pago");
        // Retorna la vista: metodosdepago.html
        return "roles/admin/crud/metododepago/metodosdepago"; 
    }

    // ------------------------------------------------------------------
    // --- 2. CREAR & ACTUALIZAR (CREATE & UPDATE) ---
    // ------------------------------------------------------------------

    // 💥 CORRECCIÓN CRÍTICA: Mapea a /nuevo para coincidir con el enlace HTML
    @GetMapping("/nuevo") 
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("metodoPago", new MetodoPago());
        model.addAttribute("titulo", "Crear Nuevo Método de Pago");
        // Retorna la vista: pagoscrear.html
        return "roles/admin/crud/metododepago/pagoscrear"; 
    }

    @PostMapping // Mapea a POST /crud/metodosdepago
    public String guardarMetodoPago(@ModelAttribute MetodoPago metodoPago, RedirectAttributes attributes) {
        try {
            metodoPagoService.guardar(metodoPago);
            String accion = (metodoPago.getIdMetodoPago() == null) ? "creado" : "actualizado";
            attributes.addFlashAttribute("success", "Método de pago " + accion + " exitosamente.");
        } catch (Exception e) {
             attributes.addFlashAttribute("error", "Error al guardar el método de pago: " + e.getMessage());
        }
        // Redirección a la lista
        return "redirect:/crud/metodosdepago"; 
    }

    @GetMapping("/editar/{id}") // Mapea a: /crud/metodosdepago/editar/{id}
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        MetodoPago metodoPago = metodoPagoService.obtenerMetodoPagoPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Método de pago no encontrado con ID: " + id));

        model.addAttribute("metodoPago", metodoPago);
        model.addAttribute("titulo", "Editar Método de Pago: " + metodoPago.getNombreMetodo());
        // Retorna la vista: pagosedit.html
        return "roles/admin/crud/metododepago/pagosedit"; 
    }

    // ------------------------------------------------------------------
    // --- 3. ELIMINAR (DELETE) ---
    // ------------------------------------------------------------------
    @GetMapping("/eliminar/{id}") // Mapea a: /crud/metodosdepago/eliminar/{id}
    public String eliminarMetodoPago(@PathVariable Integer id, RedirectAttributes attributes) {
        try {
            metodoPagoService.eliminar(id);
            attributes.addFlashAttribute("success", "Método de pago eliminado exitosamente.");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error: No se pudo eliminar el método de pago (puede estar asociado a registros).");
        }
        // Redirección a la lista
        return "redirect:/crud/metodosdepago"; 
    }

    // ------------------------------------------------------------------
    // --- 4. EXPORTACIÓN A PDF (iText 5) - ESTILIZADO ---
    // ------------------------------------------------------------------

    // Método auxiliar para agregar logo al PDF (extraído para evitar try anidado)
    private void addLogoToPDF(Document document) {
        try {
            ClassPathResource logoResource = new ClassPathResource("static/img/icono-fragata.jpg");
            InputStream logoStream = logoResource.getInputStream();
            Image logo = Image.getInstance(IOUtils.toByteArray(logoStream));
            logo.scaleToFit(100, 100); // Escalar a 100x100 píxeles
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
            document.add(new Paragraph("\n"));
        } catch (Exception e) {
            // Si no se encuentra la imagen, continuar sin ella
            System.out.println("Logo no encontrado: " + e.getMessage());
        }
    }

    @GetMapping("/export/pdf") 
    public void exportarPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=metodos_pago_" + System.currentTimeMillis() + ".pdf");

        List<MetodoPago> metodos = metodoPagoService.listarMetodosPago();
        
        Document document = new Document();
        OutputStream outputStream = null;
        
        try {
            outputStream = response.getOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Agregar imagen del logo usando método auxiliar
            addLogoToPDF(document);

            // 1. DEFINICIÓN DE FUENTES Y COLORES
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(255, 215, 0)); // Dorado
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
            
            // 3. TÍTULO
            Paragraph title = new Paragraph("Reporte de Métodos de Pago - La Fragata Giratoria", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 4. TABLA ESTILIZADA
            PdfPTable table = new PdfPTable(3); 
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 3f, 6f}); 
            table.setSpacingBefore(15);
            
            // Cabecera con color negro
            String[] headers = {"ID", "Método", "Descripción"}; 
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.BLACK); // Negro
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8);
                cell.setBorderColor(BaseColor.WHITE);
                cell.setBorderWidth(1f);
                table.addCell(cell);
            }

            // Datos con filas alternas en dorado
            boolean alternate = false;
            for (MetodoPago metodo : metodos) {
                BaseColor rowColor = alternate ? new BaseColor(255, 215, 0) : BaseColor.WHITE; // Dorado alterno
                alternate = !alternate;
                
                // Columna 1: ID
                PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(metodo.getIdMetodoPago()), dataFont));
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                idCell.setBackgroundColor(rowColor);
                idCell.setPadding(5);
                idCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(idCell);
                
                // Columna 2: Nombre del Método
                PdfPCell nombreCell = new PdfPCell(new Phrase(metodo.getNombreMetodo(), dataFont));
                nombreCell.setBackgroundColor(rowColor);
                nombreCell.setPadding(5);
                nombreCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(nombreCell); 
                
                // Columna 3: Descripción (usa getDescripcion() del modelo)
                String descripcion = (metodo.getDescripcion() != null && !metodo.getDescripcion().isEmpty()) 
                                    ? metodo.getDescripcion() : "Sin descripción";

                PdfPCell descCell = new PdfPCell(new Phrase(descripcion, dataFont));
                descCell.setBackgroundColor(rowColor);
                descCell.setPadding(5);
                descCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(descCell); 
            }

            document.add(table);
            
        } catch (Exception e) {
            throw new IOException("Error grave al generar el PDF: " + e.getMessage(), e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
            if (outputStream != null) {
                 outputStream.flush();
            }
        }
    }


    // ------------------------------------------------------------------
    // --- 5. EXPORTACIÓN A EXCEL (Apache POI) ---
    // ------------------------------------------------------------------

    // Método auxiliar para agregar logo al Excel (extraído para evitar try anidado)
    private void addLogoToExcel(Sheet sheet, Workbook workbook) {
        try {
            ClassPathResource logoResource = new ClassPathResource("static/img/icono-fragata.jpg");
            InputStream logoStream = logoResource.getInputStream();
            byte[] bytes = IOUtils.toByteArray(logoStream);
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG); // Cambiado a JPEG
            XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, 0, 3, 5); // Posición: columnas 0-3, filas 0-5
            XSSFPicture picture = drawing.createPicture(anchor, pictureIdx);
            picture.resize(3, 5); // Ajustar tamaño
        } catch (Exception e) {
            // Si no se encuentra la imagen, continuar sin ella
            System.out.println("Logo no encontrado: " + e.getMessage());
        }
    }

    @GetMapping("/export/excel") 
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=metodos_pago_" + System.currentTimeMillis() + ".xlsx");

        List<MetodoPago> metodos = metodoPagoService.listarMetodosPago();

        try (Workbook workbook = new XSSFWorkbook();
             OutputStream outputStream = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("MetodosPago");
            
            // Agregar imagen del logo usando método auxiliar
            addLogoToExcel(sheet, workbook);
            
            // Estilos para headers (negro con dorado)
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex()); // Blanco para contraste
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex()); // Negro
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Estilo para datos alternos (dorado)
            CellStyle dataStyle = workbook.createCellStyle();
            CellStyle dataAltStyle = workbook.createCellStyle();
            XSSFColor goldColor = new XSSFColor(new byte[]{(byte) 255, (byte) 215, (byte) 0}); // Dorado personalizado con byte array
            dataAltStyle.setFillForegroundColor(goldColor);
            dataAltStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Fila de Cabecera 
            Row headerRow = sheet.createRow(6); // Empezar después del logo
            String[] headers = {"ID", "Nombre del Método"}; 

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Datos de las filas
            int rowNum = 7; // Continuar después del header
            boolean alternate = false;
            for (MetodoPago metodo : metodos) {
                Row row = sheet.createRow(rowNum++);
                CellStyle currentStyle = alternate ? dataAltStyle : dataStyle;
                alternate = !alternate;

                Cell idCell = row.createCell(0);
                idCell.setCellValue(metodo.getIdMetodoPago());
                idCell.setCellStyle(currentStyle);

                Cell nombreCell = row.createCell(1);
                nombreCell.setCellValue(metodo.getNombreMetodo());
                nombreCell.setCellStyle(currentStyle);
            }

            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
