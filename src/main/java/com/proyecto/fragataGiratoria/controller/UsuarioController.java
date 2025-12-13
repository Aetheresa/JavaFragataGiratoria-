package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.model.Rol; 
import com.proyecto.fragataGiratoria.service.UsuarioService;
import com.proyecto.fragataGiratoria.service.RolService; 

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; 
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream; 

import jakarta.servlet.http.HttpServletResponse;

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

import org.springframework.core.io.ClassPathResource;

@Controller
@RequestMapping("/crud/usuarios") 
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    // Constructor con inyección de servicios
    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    // ------------------------------------------------------------------
    // --- MÉTODOS CRUD (Gestión de Vistas Thymeleaf) ---
    // ------------------------------------------------------------------

    @GetMapping
    public String listarAdmin(Model model) {
        List<Usuario> usuarios = usuarioService.listar();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        return "roles/admin/crud/usuarios/usuarios"; 
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());

        // ✅ CORREGIDO: Se llama a obtenerRoles() que SÍ existe en RolService
        List<Rol> roles = rolService.obtenerRoles();
        model.addAttribute("roles", roles);

        return "roles/admin/crud/usuarios/usuarioscrear"; 
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario) {
        usuarioService.guardar(usuario);
        return "redirect:/crud/usuarios";
    }

    @GetMapping("/editar/{idUsuario}")
    public String editar(@PathVariable Integer idUsuario, Model model) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(idUsuario);

        if (usuarioOpt.isEmpty()) {
            return "redirect:/crud/usuarios"; 
        }

        model.addAttribute("usuario", usuarioOpt.get());
        
        // ✅ CORREGIDO: Se llama a obtenerRoles()
        List<Rol> roles = rolService.obtenerRoles();
        model.addAttribute("roles", roles);

        return "roles/admin/crud/usuarios/usuariosedit"; 
    }

    @GetMapping("/eliminar/{idUsuario}")
    public String eliminar(@PathVariable Integer idUsuario) {
        usuarioService.eliminar(idUsuario);
        return "redirect:/crud/usuarios";
    }
    
    // ------------------------------------------------------------------
    // --- MÉTODOS DE EXPORTACIÓN (PDF e Excel) ---
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
    public void exportarUsuariosPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=usuarios_" + System.currentTimeMillis() + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Usuario> listaUsuarios = usuarioService.listar();
        
        Document document = new Document();
        OutputStream outputStream = null;
        
        try {
            outputStream = response.getOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Agregar imagen del logo usando método auxiliar
            addLogoToPDF(document);
            
            // Fuentes
            Font fontTitle = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(255, 215, 0)); // Dorado
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            Font fontData = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
            
            Paragraph title = new Paragraph("Reporte de Usuarios - La Fragata Giratoria", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n")); 

            // Tabla (5 columnas: ID, Nombre de Usuario, Email, Rol, Estado)
            PdfPTable table = new PdfPTable(5); 
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 4f, 4f, 2f, 2f}); 

            // Headers con color negro
            String[] headers = {"ID", "Nombre de Usuario", "Email", "Rol", "Estado"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLACK); // Negro
                cell.setPadding(8); 
                cell.setBorderColor(BaseColor.WHITE);
                cell.setBorderWidth(1f);
                table.addCell(cell);
            }

            // Datos PDF con filas alternas
            boolean alternate = false;
            for (Usuario usuario : listaUsuarios) {
                BaseColor rowColor = alternate ? new BaseColor(255, 215, 0) : BaseColor.WHITE; // Dorado alterno
                alternate = !alternate;
                
                // ID
                PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(usuario.getIdUsuario()), fontData));
                idCell.setPadding(5);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                idCell.setBackgroundColor(rowColor);
                idCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(idCell);

                // Nombre de Usuario (getNombreUsuario())
                String nombreUsuario = (usuario.getNombreUsuario() != null) ? usuario.getNombreUsuario() : "N/D";
                PdfPCell nombreCell = new PdfPCell(new Phrase(nombreUsuario, fontData));
                nombreCell.setPadding(5);
                nombreCell.setBackgroundColor(rowColor);
                nombreCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(nombreCell);
                
                // Email
                PdfPCell emailCell = new PdfPCell(new Phrase(usuario.getEmail() != null ? usuario.getEmail() : "N/D", fontData));
                emailCell.setPadding(5);
                emailCell.setBackgroundColor(rowColor);
                emailCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(emailCell);
                
                // Rol (Usa getNombreRol())
                String rol = (usuario.getRol() != null && usuario.getRol().getNombreRol() != null) 
                            ? usuario.getRol().getNombreRol() : "N/D"; 
                PdfPCell rolCell = new PdfPCell(new Phrase(rol, fontData));
                rolCell.setPadding(5);
                rolCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                rolCell.setBackgroundColor(rowColor);
                rolCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(rolCell);

                // Estado (Usa .toString() para el Enum)
                String estado = (usuario.getEstado() != null) ? usuario.getEstado().toString() : "N/D";
                PdfPCell estadoCell = new PdfPCell(new Phrase(estado, fontData));
                estadoCell.setPadding(5);
                estadoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                estadoCell.setBackgroundColor(rowColor);
                estadoCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(estadoCell);
            }

            document.add(table);
            
        } catch (Exception e) { 
             e.printStackTrace();
        } finally {
            if (document.isOpen()) {
                document.close();
            }
            if (outputStream != null) {
                 outputStream.flush();
            }
        }
    }

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
    public void exportarUsuariosExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=usuarios_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Usuario> listaUsuarios = usuarioService.listar();

        try (Workbook workbook = new XSSFWorkbook(); 
             OutputStream outputStream = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("Usuarios");
            
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

            Row headerRow = sheet.createRow(6); // Empezar después del logo
            
            // Headers
            String[] headers = {"ID", "Nombre de Usuario", "Email", "Rol", "Estado"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i); 
            }
            
            int rowCount = 7; // Continuar después del header
            boolean alternate = false;
            // Datos Excel
            for (Usuario usuario : listaUsuarios) {
                Row dataRow = sheet.createRow(rowCount++);
                CellStyle currentStyle = alternate ? dataAltStyle : dataStyle;
                alternate = !alternate;
                int columnCount = 0;

                Cell idCell = dataRow.createCell(columnCount++);
                idCell.setCellValue(usuario.getIdUsuario());
                idCell.setCellStyle(currentStyle);
                
                // Nombre de Usuario (getNombreUsuario())
                String nombreUsuario = usuario.getNombreUsuario() != null ? usuario.getNombreUsuario() : "N/D";
                Cell nombreCell = dataRow.createCell(columnCount++);
                nombreCell.setCellValue(nombreUsuario);
                nombreCell.setCellStyle(currentStyle);
                
                Cell emailCell = dataRow.createCell(columnCount++);
                emailCell.setCellValue(usuario.getEmail() != null ? usuario.getEmail() : "N/D");
                emailCell.setCellStyle(currentStyle);
                
                // Rol (Usa getNombreRol())
                String rol = (usuario.getRol() != null && usuario.getRol().getNombreRol() != null) 
                             ? usuario.getRol().getNombreRol() : "N/D";
                Cell rolCell = dataRow.createCell(columnCount++);
                rolCell.setCellValue(rol);
                rolCell.setCellStyle(currentStyle);

                // Estado (Usa .toString() para el Enum)
                String estado = (usuario.getEstado() != null) ? usuario.getEstado().toString() : "N/D";
                Cell estadoCell = dataRow.createCell(columnCount++);
                estadoCell.setCellValue(estado);
                estadoCell.setCellStyle(currentStyle);
            }

            workbook.write(outputStream);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
