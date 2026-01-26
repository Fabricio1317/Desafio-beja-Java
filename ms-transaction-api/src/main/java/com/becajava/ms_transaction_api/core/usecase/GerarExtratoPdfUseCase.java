package com.becajava.ms_transaction_api.core.usecase;

import com.becajava.ms_transaction_api.core.domain.Transacao;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class GerarExtratoPdfUseCase {

    public byte[] gerar(List<Transacao> transacoes) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();


            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph titulo = new Paragraph("Extrato Bancário - Beca Java", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 1.5f, 1.5f, 2f, 2f, 2.5f});

            adicionarCabecalho(table);
            adicionarDados(table, transacoes);

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }

        return out.toByteArray();
    }

    private void adicionarCabecalho(PdfPTable table) {
        String[] colunas = {"ID", "De", "Para", "Valor", "Tipo", "Data"};


        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

        for (String coluna : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(coluna, fontHeader));

            cell.setBackgroundColor(new BaseColor(0, 51, 102));
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void adicionarDados(PdfPTable table, List<Transacao> transacoes) {
        Font fontDados = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DateTimeFormatter dataFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Transacao t : transacoes) {
            table.addCell(new Phrase(t.getId().toString(), fontDados));
            table.addCell(new Phrase(t.getPagadorId().toString(), fontDados));
            table.addCell(new Phrase(t.getRecebedorId().toString(), fontDados));

            if (t.getValor() != null) {
                table.addCell(new Phrase(moeda.format(t.getValor()), fontDados));
            } else {
                table.addCell(new Phrase("R$ 0,00", fontDados));
            }

            table.addCell(new Phrase(t.getTipo() != null ? t.getTipo() : "-", fontDados));

            String dataStr = (t.getDataCriacao() != null) ? t.getDataCriacao().format(dataFmt) : "N/A";
            table.addCell(new Phrase(dataStr, fontDados));
        }
    }
}