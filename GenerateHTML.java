
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;



public class GenerateHTML {

    public static HashMap<String, String> valHM = new HashMap<>();
    public static HashMap<String, String> sqlHM = new HashMap<>();
    public static boolean hasString = false;
    public static boolean hasInt = false;

    public static HashMap<String, String> GenerateHTML_EP(String inputFileName) {
        File obj=new File("ParsedHTML.html");
        if(obj!=null)obj.delete();
        writeToFile("<html>");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFileName);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE
                        && !Objects.equals(node.getNodeName(), "break")) {

                    NodeList childNodes = node.getChildNodes();

                    switch (node.getNodeName()) {
                        case "title":
                            isTitle(childNodes);
                            break;
                        case "textbox":
                            if (node.hasAttributes()) {
                                pushToHM(node);
                            }
                            isTextbox(childNodes);
                            break;
                        case "checkboxes":
                        case "radio":
                            isElementSelect(childNodes);
                            break;
                        case "multiselect":
                            isMultiSelect(childNodes);
                            break;
                        case "select":
                            isSelect(childNodes);
                            break;
                        case "reset":
                        case "submit":
                            isButton(childNodes, node.getNodeName());
                            break;
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        writeToFile("</table></form>");
        genScript();
        writeToFile("</body></html>");
        return sqlHM;
    }

    public static void genScript() {
        writeToFile("<script type =\"text/javascript\">");
        writeToFile("function isValid(){");
        if (hasInt) {
            writeToFile("var character = new RegExp(/^[A-Za-z]+$/);");
        } else if (hasString) {
            writeToFile("var digit = new RegExp(/^\\d+$/);");
        }
        Set set = valHM.entrySet();
        for (Object aSet : set) {
            Map.Entry me = (Map.Entry) aSet;
            writeToFile("if(document.ParseXML." + me.getKey() + ".value==\"\"){\n" +
                    "alert(\"" + me.getKey() + "\" cannot be empty );\n" +
                    "return false;}\n");
            if (me.getValue().equals("string")) {
                writeToFile("if(digit.test(document.ParseXML." + me.getKey() + ".value)){\n" +
                        "alert(\"Numeric values not accepted in " + me.getKey() + "\");\n" +
                        "return false;}\n");
            } else if (me.getValue().equals("integer")) {
                writeToFile("if(character.test(document.ParseXML." + me.getKey() + ".value)){\n" +
                        "alert(\"Alphabets are not accepted in" + me.getKey() + "\");\n" +
                        "return false;}\n");
            }
        }
        writeToFile("}</script>");
    }

    public static void pushToHM(Node node) {
        String id;
        id = "";
        NodeList childNodes = node.getChildNodes();
        org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
        String dataType = nodeElement.getAttribute("datatype").toLowerCase();
        String key;
        key = nodeElement.getAttribute("key").toLowerCase();
        if (Objects.equals(key, "key")) {
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node n = childNodes.item(j);
                if (n.getNodeType() == Node.ELEMENT_NODE) switch (n.getNodeName()) {
                    case "name":
                        sqlHM.put("key", n.getLastChild().getTextContent());
                        break;
                }
            }
        }
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node n = childNodes.item(j);
            if (n.getNodeType() == Node.ELEMENT_NODE) switch (n.getNodeName()) {
                case "name":
                    id = n.getLastChild().getTextContent();
                    break;
            }
        }
        if (!hasString) {
            hasString = (dataType.equals("string"));
        }
        if (!hasInt) {
            hasInt = (dataType.equals("integer"));
        }
        valHM.put(id, dataType);
    }

    public static void isButton(NodeList childNodes, String parentType) {
        String caption;
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node n = childNodes.item(j);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                switch (n.getNodeName()) {
                    case "caption":
                        caption = n.getLastChild().getTextContent();
                        writeToFile("<tr><td></td><td><input type=\"" + parentType + "\" value=\"" + caption + "\"></td></tr>");
                        break;
                }
            }
        }
    }

    public static void isSelect(NodeList childNodes) {
        String name = "";
        String caption;
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node n = childNodes.item(j);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                switch (n.getNodeName()) {
                    case "name":
                        name = n.getLastChild().getTextContent();
                        sqlHM.put(name, n.getParentNode().getNodeName());
                        break;
                    case "caption":
                        caption = n.getLastChild().getTextContent();
                        writeToFile("<tr><td>" + caption + "</td><td>");
                        break;
                    case "options":
                        writeToFile("<select name=\"" + name + "\">");
                        NodeList multiSelectChild = n.getChildNodes();
                        isOptions(multiSelectChild);
                        break;
                }
            }
        }
        String code = "</select></td></tr>";
        writeToFile(code);
    }

    public static void isElementSelect(NodeList childNodes) {
        String name = "";
        String caption;
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node n = childNodes.item(j);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                switch (n.getNodeName()) {
                    case "name":
                        name = n.getLastChild().getTextContent();
                        sqlHM.put(name, n.getParentNode().getNodeName());
                        break;
                    case "caption":
                        caption = n.getLastChild().getTextContent();
                        writeToFile("<tr><td>" + caption + "</td><td>");
                        break;
                    case "checkboxgroup":
                    case "radiogroup":
                        NodeList ElementChild = n.getChildNodes();
                        isElementGroup(ElementChild, name);
                        break;
                }
            }
        }
        writeToFile("</td></tr>");
    }

    public static void isElementGroup(NodeList childNodes, String arg) {
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node n = childNodes.item(j);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                switch (n.getNodeName()) {
                    case "checkbox":
                    case "radioelement":
                        NodeList Element = n.getChildNodes();
                        ElementItems(Element, arg, n.getNodeName());
                        break;
                }
            }
        }

    }

    public static void ElementItems(NodeList Items, String arg, String parentType) {
        String value = "";
        String caption = "";
        String type;
        for (int j = 0; j < Items.getLength(); j++) {
            Node n = Items.item(j);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                switch (n.getNodeName()) {
                    case "value":
                        value = n.getLastChild().getTextContent();
                        break;
                    case "caption":
                        caption = n.getLastChild().getTextContent();
                        break;
                }
            }
        }
        if (Objects.equals(parentType, "checkbox")) {
            type = "checkbox";
            arg+="[]";
        } else {
            type = "radio";
        }
        String code = "<input type=\"" + type + "\" name=\"" + arg + "\" value=\"" + value + "\" >" + caption + "<br>";
        writeToFile(code);
    }

    public static void isMultiSelect(NodeList childNodes) {
        String name = "";
        String caption;
        String size = "";
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node n = childNodes.item(j);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                switch (n.getNodeName()) {
                    case "name":
                        name = n.getLastChild().getTextContent();
                        sqlHM.put(name, "multiselect");
                        break;
                    case "caption":
                        caption = n.getLastChild().getTextContent();
                        writeToFile("<tr><td>" + caption + "</td><td>\n");
                        break;
                    case "size":
                        size = n.getLastChild().getTextContent();
                        break;
                    case "options":
                        writeToFile("<select name=\"" + name + "[]\" multiple size=" + size + ">");
                        NodeList multiSelectChild = n.getChildNodes();
                        isOptions(multiSelectChild);
                        break;
                }
            }
        }
        String code = "</select></td></tr>";
        writeToFile(code);
    }

    public static void isOptions(NodeList childNodes) {
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node n = childNodes.item(j);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                switch (n.getNodeName()) {
                    case "option":
                        NodeList optionsChild = n.getChildNodes();
                        isOption(optionsChild);
                        break;
                }
            }
        }
    }

    public static void isOption(NodeList childNodes) {
        String value = "";
        String caption = "";
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node n = childNodes.item(j);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                switch (n.getNodeName()) {
                    case "value":
                        value = n.getLastChild().getTextContent();
                        break;
                    case "caption":
                        caption = n.getLastChild().getTextContent();
                        break;
                }
            }
        }
        String code = "<option value=\"" + value + "\">" + caption + "</option>";
        writeToFile(code);
    }

    public static void isTextbox(NodeList childNodes) {
        String name = "";
        String caption = "";
        String size = "";
        String maxlength = "";
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node n = childNodes.item(j);
            if (n.getNodeType() == Node.ELEMENT_NODE) {

                switch (n.getNodeName()) {
                    case "name":
                        name = n.getLastChild().getTextContent();
                        sqlHM.put(name, "textbox");
                        //System.out.println(name);
                        break;
                    case "caption":
                        caption = n.getLastChild().getTextContent();
                        //System.out.println(caption);
                        break;
                    case "size":
                        size = n.getLastChild().getTextContent();
                        break;
                    case "maxlength":
                        maxlength = n.getLastChild().getTextContent();
                        break;
                }
            }
        }


        String code = "<tr><td>" + caption
                + "</td><td><input type=\"text\" id =" + "\"" + name + "\""
                + " size =" + "\"" + size + "\"" + " name =" + "\"" + name
                + "\"" + " maxlength =" + "\"" + maxlength + "\""
                + "/></td></tr>";
        writeToFile(code);

    }

    public static void isTitle(NodeList childNodes) {

        String title = childNodes.item(1).getLastChild().getTextContent();

        writeToFile("<head>");
        writeToFile("<title>" + title + "</title>");
        writeToFile("</head>");
        writeToFile("<body>\n" + "<h1>" + title + "</h1>");
        writeToFile("<form name=\"ParseXML\" method=\"post\"  action = \"ParsedPHP.php\" onsubmit=\"return isValid();\">");
        writeToFile("<table>");
        sqlHM.put("tableName",title.replaceAll(" ",""));
    }

    public static void writeToFile(String inp) {
        PrintWriter fileWriter = null;
        try {
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter("ParsedHTML.html", true)));
            fileWriter.println(inp);
        } catch (IOException E) {
            E.printStackTrace();
        }

        if (fileWriter != null) {
            fileWriter.close();
        }

    }


}
