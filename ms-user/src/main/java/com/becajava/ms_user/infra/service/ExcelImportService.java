package com.becajava.ms_user.infra.service;

import com.becajava.ms_user.core.usecase.CriarUsuarioUseCase;
import com.becajava.ms_user.dto.UsuarioRequestDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImportService {

    private final CriarUsuarioUseCase criarUsuarioUseCase;

    public ExcelImportService(CriarUsuarioUseCase criarUsuarioUseCase) {
        this.criarUsuarioUseCase = criarUsuarioUseCase;
    }

    public List<String> importarUsuarios(MultipartFile file) {
        List<String> logErros = new ArrayList<>();
        int linhasSucesso = 0;

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                try {
                    String nome = getCellValue(row, 0);
                    String cpf = getCellValue(row, 1);
                    String email = getCellValue(row, 2);
                    String senha = getCellValue(row, 3);

                    if(nome.isBlank() || cpf.isBlank() || email.isBlank()) continue;


                    UsuarioRequestDTO dto = new UsuarioRequestDTO(nome, cpf, email, senha);

                    criarUsuarioUseCase.execute(dto);
                    linhasSucesso++;

                } catch (Exception e) {
                    logErros.add("Linha " + (row.getRowNum() + 1) + " falhou: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler arquivo Excel: " + e.getMessage());
        }

        logErros.add(0, "Importação finalizada! Usuários cadastrados com sucesso: " + linhasSucesso);
        return logErros;
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
