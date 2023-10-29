package learn.test.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class RichTextLearnTest {
    @Test
    public void RichTextLearn() throws IOException {
        String html = "<html> <head> <title>Example Page</title> </head> <body> <table id=\"myTable\"> <tr> <td>Row 1 Cell 1</td> <td>Row 1 Cell 2</td> </tr> <tr> <td>Row 2 Cell 1</td> <td>Row 2 Cell 2</td> </tr> <tr> <td>Row 3 Cell 1</td> <td>Row 3 Cell 2</td> </tr> </table> </body> </html>";
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        Element copiedTable=table.clone();
        Elements rows = copiedTable.select("tr");
        rows.get(1).select("td").forEach(cell -> cell.text("New Content"));
        doc.select("body").last().appendChild(copiedTable);
        System.out.println(doc.html());

    }
}