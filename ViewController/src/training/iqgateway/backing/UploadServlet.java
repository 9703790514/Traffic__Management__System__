package training.iqgateway.backing;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import training.iqgateway.entities.Offense;
import training.iqgateway.services.RtoSessionEJBLocal;

public class UploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (ServletFileUpload.isMultipartContent(request)) {
            try {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List<FileItem> items = upload.parseRequest(request);

                String offenceStatus = null, place = null, timeString = null, vehNo = null, username = null;
                Long offenseDetailsId = null;
                byte[] imageBytes = null;

                for (FileItem item : items) {
                    if (item.isFormField()) {
                        String field = item.getFieldName();
                        if ("offenceStatus".equals(field)) {
                            offenceStatus = item.getString();
                        } else if ("place".equals(field)) {
                            place = item.getString();
                        } else if ("timeString".equals(field)) {
                            timeString = item.getString();
                        } else if ("vehNo".equals(field)) {
                            vehNo = item.getString();
                        } else if ("username".equals(field)) {
                            username = item.getString();
                        } else if ("offenseDetailsId".equals(field)) {
                            offenseDetailsId = Long.valueOf(item.getString());
                        }
                    } else if ("imageFile".equals(item.getFieldName())) {
                        imageBytes = item.get();
                    }
                }

                // Create and persist Offense
                InitialContext ic = new InitialContext();
                RtoSessionEJBLocal session = (RtoSessionEJBLocal) ic.lookup("java:comp/env/ejb/local/RtoSessionEJB");

                Offense newOffense = new Offense();
                newOffense.setOffenceStatus(offenceStatus);
                newOffense.setPlace(place);
                newOffense.setTime(new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeString).getTime()));
                if (vehNo != null) newOffense.setVehicleApplication(session.findVehicleByNo(vehNo));
                if (username != null) newOffense.setUsers(session.findUserByUsername(username));
                if (offenseDetailsId != null) newOffense.setOffenseDetails(session.findOffenseDetailsById(offenseDetailsId));
                if (imageBytes != null && imageBytes.length > 0) newOffense.setImage(imageBytes);

                session.persistOffense(newOffense);

                // Redirect to success page or offense list
                response.sendRedirect("offense_success.jsf");
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Add failed: " + e.getMessage());
                request.getRequestDispatcher("offense_add.jspx").forward(request, response);
            }
        }
    }
}
