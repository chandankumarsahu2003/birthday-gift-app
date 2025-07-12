import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SaveData extends HttpServlet {
    private static final String filePath = "D:\\payment_data.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name").trim();
        String village = request.getParameter("village").trim();
        String payment = request.getParameter("payment").trim();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // ✅ Check for empty input
        if (name.isEmpty() || village.isEmpty() || payment.isEmpty()) {
            out.println("<html><body style='font-family:Segoe UI; background-color:#fff0f5;'>");
            out.println("<h3 style='color:red;'>❌ Please fill in all fields.</h3>");
            out.println("<a href='index.html'>⬅️ Back to Form</a>");
            out.println("</body></html>");
            return;
        }

        // ✅ Generate current date-time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String dateTime = now.format(formatter);

        // ✅ Load previous entries (if any)
        List<String[]> entries = new ArrayList<>();
        File file = new File(filePath);

        if (file.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean skipHeader = true;

            while ((line = br.readLine()) != null) {
                if (skipHeader) {
                    if (line.startsWith("#")) {
                        skipHeader = false;
                    }
                    continue;
                }

                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                // Extract each column from formatted lines
                if (line.matches("^\\d+\\s+.*")) {
                    String number = line.substring(0, 4).trim();
                    String nameField = line.substring(4, 24).trim();
                    String villageField = line.substring(24, 44).trim();
                    String paymentField = line.substring(44, 54).trim();
                    String dateField = line.substring(54).trim();

                    entries.add(new String[]{nameField, villageField, paymentField, dateField});
                }
            }
            br.close();
        }

        // ✅ Add new entry with date-time
        entries.add(new String[]{name, village, "₹" + payment, dateTime});

        // ✅ Save all entries to file (overwrite)
        PrintWriter pw = new PrintWriter(new FileWriter(filePath, false));
        pw.println("🎉 Birthday Gift List 🎁\n");
        pw.printf("%-4s%-20s%-20s%-10s%-20s%n", "#", "Name", "Village", "Payment", "DateTime");

        int count = 1;
        for (String[] e : entries) {
            pw.printf("%-4d%-20s%-20s%-10s%-20s%n", count++, e[0], e[1], e[2], e[3]);
        }
        pw.close();

        // ✅ Show entries in browser
        out.println("<html><body style='background-color:#fff0f5; font-family:Segoe UI;'>");
        out.println("<h2 style='color:#cc0066;'>🎉 Birthday Gift List 🎁</h2>");
        out.println("<table border='1' cellpadding='10' style='border-collapse:collapse;'>");
        out.println("<tr><th>#</th><th>Name</th><th>Village</th><th>Payment</th><th>Date & Time</th></tr>");

        int i = 1;
        for (String[] entry : entries) {
            out.println("<tr>");
            out.println("<td>" + (i++) + "</td>");
            out.println("<td>" + entry[0] + "</td>");
            out.println("<td>" + entry[1] + "</td>");
            out.println("<td>" + entry[2] + "</td>");
            out.println("<td>" + entry[3] + "</td>");
            out.println("</tr>");
        }

        out.println("</table>");
        out.println("<br><a href='index.html'>⬅️ Back to Form</a>");
        out.println("</body></html>");
    }
}
