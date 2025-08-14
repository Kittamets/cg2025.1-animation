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
                    javaCode.append(String.format("currentX = %f;\ncurrentY = %f;\n", currentX, currentY));
                    break;

                case "c": // Cubic Bézier Curve (relative)
                    javaCode.append(String.format("bezierCurve(g2d, currentX, currentY, "
                            + "%f + currentX, %f + currentY, "
                            + "%f + currentX, %f + currentY, "
                            + "%f + currentX, %f + currentY, 3);\n",
                            Double.parseDouble(values[0]), Double.parseDouble(values[1]),
                            Double.parseDouble(values[2]), Double.parseDouble(values[3]),
                            Double.parseDouble(values[4]), Double.parseDouble(values[5])));
                    currentX += Double.parseDouble(values[4]);
                    currentY += Double.parseDouble(values[5]);
                    javaCode.append(String.format("currentX = %f; currentY = %f;\n", currentX, currentY));
                    break;

                case "L": // Line To (absolute)
                    javaCode.append(String.format("bresenhamLine(g2d, (int) currentX, (int) currentY, "
                            + "(int) (%f), (int) (%f));\n",
                            Double.parseDouble(values[0]), Double.parseDouble(values[1])));
                    currentX = Double.parseDouble(values[0]);
                    currentY = Double.parseDouble(values[1]);
                    javaCode.append(String.format("currentX = %f; currentY = %f;\n", currentX, currentY));
                    break;

                case "l": // Line To (relative)
                    javaCode.append(String.format("bresenhamLine(g2d, (int) currentX, (int) currentY, "
                            + "(int) (currentX + %f), (int) (currentY + %f));\n",
                            Double.parseDouble(values[0]), Double.parseDouble(values[1])));
                    currentX += Double.parseDouble(values[0]);
                    currentY += Double.parseDouble(values[1]);
                    javaCode.append(String.format("currentX = %f; currentY = %f;\n", currentX, currentY));
                    break;

                case "v": // Vertical Line (relative)
                    javaCode.append(String.format("bresenhamLine(g2d, (int) currentX, (int) currentY, "
                            + "(int) currentX, (int) (currentY + %f));\n",
                            Double.parseDouble(values[0])));
                    currentY += Double.parseDouble(values[0]);
                    javaCode.append(String.format("currentY = %f;\n", currentY));
                    break;

                case "h": // Horizontal Line (relative)
                    javaCode.append(String.format("bresenhamLine(g2d, (int) currentX, (int) currentY, "
                            + "(int) (currentX + %f), (int) currentY);\n",
                            Double.parseDouble(values[0])));
                    currentX += Double.parseDouble(values[0]);
                    javaCode.append(String.format("currentX = %f;\n", currentX));
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
        String svgPath = "M 367.65 283.5 c 0.4 1.31 -1.45 1.22 -1.53 1.83 c -0.06 0.5 0.48 1.26 0.38 1.85 c -0.16 0.93 -0.85 3.38 -1.24 4.18 c -2.01 4.16 -11.38 4.71 -14.09 1.15 l 3.49 0.49 l -1.5 -7.5 c -1.7 -0.41 -0.79 1.91 -1.1 3.15 c -0.26 1.05 -3.05 4.56 -3.39 2.59 c -0.13 -0.76 4.04 -5.99 2.01 -6.99 c -0.03 -1.24 6.9 -0.66 8.25 -0.73 c 2.82 -0.14 5.86 -0.91 8.72 -0.02 Z M 364.66 285.5 l -4.5 -0.5 c 0.92 2.84 1.28 5.15 0 7.99 c 2.84 1.62 4.93 -7.07 4.5 -7.49 Z";

        // แปลง SVG path string เป็น Java code
        String javaCode = parseSVGPathToJavaCode(svgPath);

        // แสดงผลลัพธ์ที่ได้
        System.out.println("Generated Java Code for SVG path:");
        System.out.println(javaCode);
    }
}
