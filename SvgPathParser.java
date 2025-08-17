import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SvgPathParser {

    // Method สำหรับแปลง SVG path string ให้เป็นคำสั่ง Java ที่ใช้ได้ใน paintComponent
    public static String parseSVGPathToJavaCode(String svgPath) {
        StringBuilder javaCode = new StringBuilder();

        // เริ่มต้นตั้งตำแหน่งปากกา
        double currentX = 0;
        double currentY = 0;

        // ใช้ Regular Expression เพื่อจับคำสั่ง SVG
        Pattern pattern = Pattern
                .compile("([a-zA-Z])\\s*(-?\\d*\\.?\\d+\\s*-?\\d*\\.?\\d+\\s*(-?\\d*\\.?\\d+\\s*-?\\d*\\.?\\d+\\s*)*)");
        Matcher matcher = pattern.matcher(svgPath);

        while (matcher.find()) {
            String command = matcher.group(1);
            String[] values = matcher.group(2).trim().split("\\s+");

            switch (command) {
                case "M": // Move To (absolute)
                    currentX = Double.parseDouble(values[0]);
                    currentY = Double.parseDouble(values[1]);
                    break;

                case "c": // Cubic Bézier Curve (relative)
                    double x1 = currentX + Double.parseDouble(values[0]);
                    double y1 = currentY + Double.parseDouble(values[1]);
                    double x2 = currentX + Double.parseDouble(values[2]);
                    double y2 = currentY + Double.parseDouble(values[3]);
                    double x = currentX + Double.parseDouble(values[4]);
                    double y = currentY + Double.parseDouble(values[5]);

                    javaCode.append(String.format("bezierCurve(g2d, %f, %f, "
                            + "%f, %f, "
                            + "%f, %f, "
                            + "%f , %f , 3);\n",
                            currentX, currentY,
                            x1, y1,
                            x2, y2,
                            x, y));
                    currentX += Double.parseDouble(values[4]);
                    currentY += Double.parseDouble(values[5]);
                    break;

                case "l": // Line To (relative)
                    double xs = currentX;
                    double ys = currentY;
                    double xd = currentX + Double.parseDouble(values[0]);
                    double yd = currentY + Double.parseDouble(values[1]);
                    javaCode.append(String.format("bresenhamLine(g2d, %f, %f, "
                            + "%f, %f);\n",
                            xs, ys,
                            xd, yd));
                    currentX += Double.parseDouble(values[0]);
                    currentY += Double.parseDouble(values[1]);
                    break;

                case "v": // Vertical Line (relative)
                    double desy = currentY + Double.parseDouble(values[0]);
                    javaCode.append(String.format("bresenhamLine(g2d, %f, %f, "
                            + "%f, %f);\n",
                            currentX, currentY,
                            currentX, desy));
                    currentY += Double.parseDouble(values[0]);
                    break;

                case "h": // Horizontal Line (relative)
                    double newX = currentX + Double.parseDouble(values[0]);
                    javaCode.append(String.format("bresenhamLine(g2d, %f, %f, "
                            + "%f, %f);\n",
                            currentX, currentY,
                            newX, currentY));
                    currentX += Double.parseDouble(values[0]);
                    break;

                default:
                    break;
            }
        }
        return javaCode.toString();
    }

    // main method for testing
    public static void main(String[] args) {
        // ตัวอย่าง SVG path string
        String svgPath = "";

        // แปลง SVG path string เป็น Java code
        String javaCode = parseSVGPathToJavaCode(svgPath);

        // แสดงผลลัพธ์ที่ได้
        System.out.println("Generated Java Code for SVG path:");
        System.out.println(javaCode);
    }
}
