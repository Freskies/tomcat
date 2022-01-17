package tomcat.robaz;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet (name = "helloServlet", value = "/hello-servlet/*")
public class HelloServlet extends HttpServlet {
    private final HashMap<String, String> users = new HashMap<> ();

    public void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType ("text/html");
        PrintWriter out = response.getWriter ();

        String functionName = request.getPathInfo ().split ("/")[1];
        try {
            switch (functionName) {
                case "index": {
                    out.println (this.getFile ("index", "html"));
                    break;
                }
                case "login": {
                    String name = request.getParameter ("name");
                    String pass = request.getParameter ("pass");
                    String image = "";
                    if (request.getParameter ("image") != null)
                        image = request.getParameter ("image");

                    out.println (this.getFile ("Services/login", "html",
                            this.login (name, pass) ? "Successfully logged in" : "You are not registered", image));
                    break;
                }
                case "register": {
                    String name = request.getParameter ("name");
                    String pass = request.getParameter ("pass");
                    this.register (name, pass);
                    out.println (this.getFile ("Services/register", "html","Successfully registered"));
                    break;
                }
                case "delete": {
                    String name = request.getParameter ("name");
                    String pass = request.getParameter ("pass");
                    out.println (this.getFile ("Services/delete", "html",
                            this.delete (name, pass) ? "Successfully deleted" : "You are not registered"));
                    break;
                }
                default: {
                    try {
                        response.setContentType ("image/png");
                        out.println (this.getFile ("Images/" + functionName, "png"));
                    } catch (Exception ignored) {
                        out.println (this.getFile ("Error", "html", "There's no functions with this name"));
                    }
                }
            }
        } catch (Exception ignored) {
            out.println (this.getFile ("Error", "html", "Parameters are wrong"));
        }
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doGet (request, response);
    }

    public void destroy () {
    }

    public String getFile(String fileName, String extension, Object... parameters) {
        try  {
            InputStream inputStream = this.getServletContext ().getResourceAsStream
                    ("/WEB-INF/" + fileName + "." + extension);
            switch (extension) {
                case "html": {
                    return String.format (new String (inputStream.readAllBytes (), StandardCharsets.UTF_8), parameters);
                }
                case "png": {
                    return new String (inputStream.readAllBytes (), StandardCharsets.ISO_8859_1);
                }
                default: {
                    return new String (inputStream.readAllBytes (), StandardCharsets.UTF_8);
                }
            }
        } catch (Exception ignored) {
            return this.getFile ("Error", "html","a general error is occurred");
        }
    }

    private void register (String name, String password) {
        this.users.put (name, password);
    }

    private boolean login (String name, String password) {
       return password.equals (users.get (name));
    }

    private boolean delete (String name, String password) {
        if (this.login (name, password)){
            this.users.remove (name, password);
            return true;
        }
        return false;
    }
}