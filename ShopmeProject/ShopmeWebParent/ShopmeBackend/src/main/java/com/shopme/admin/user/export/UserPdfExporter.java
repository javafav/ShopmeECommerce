package com.shopme.admin.user.export;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopme.admin.user.AbstractExporter;
import com.shopme.common.entity.User;

public class UserPdfExporter extends AbstractExporter {

	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/pdf", ".pdf");

		Document document = new Document(PageSize.A4);

		ServletOutputStream outputStream = response.getOutputStream();

		PdfWriter.getInstance(document, outputStream);
		document.open();
		Font font = FontFactory.getFont(FontFactory.TIMES_BOLD);
		font.setColor(Color.BLACK);
		font.setSize(18);
		Paragraph paragraph = new Paragraph("List of User", font);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);

		PdfPTable table = new PdfPTable(6);

		table.setWidthPercentage(100f);
		table.setSpacingBefore(10);
		table.setWidths(new float[] { 1.0f, 5.5f, 2.5f, 2.5f, 3.0f, 1.7f });

		writeTableHeader(table);
		writeTableData(table, listUsers);
		document.add(paragraph);
		document.add(table);

		document.close();

	}

	private void writeTableData(PdfPTable table, List<User> listUsers) {
		PdfPCell cell = new PdfPCell();
		cell.setPadding(2);
		cell.setSpaceCharRatio(0.5f);
		for (User user : listUsers) {

			cell.setPhrase(new Phrase(String.valueOf(user.getId())));
			table.addCell(cell);
			cell.setPhrase(new Phrase(user.getEmail()));
			table.addCell(cell);
			cell.setPhrase(new Phrase(user.getFirstName()));
			table.addCell(cell);
			cell.setPhrase(new Phrase(user.getLastName()));
			table.addCell(cell);
			cell.setPhrase(new Phrase(user.getRoles().toString()));
			table.addCell(cell);
			cell.setPhrase(new Phrase(String.valueOf(user.isEnabled())));
			table.addCell(cell);

		}

	}

	private void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();
		Color color = new Color(0, 0, 0, 0.3f);
		cell.setBackgroundColor(color);
		cell.setPadding(5);

		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setColor(Color.WHITE);

		cell.setPhrase(new Phrase("ID", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("E-mail", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("First Name", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Last Name", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Roles", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Enabled", font));
		table.addCell(cell);

	}

}
