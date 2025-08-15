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
                    // javaCode.append(String.format("currentX = %f;\ncurrentY = %f;\n", currentX, currentY));
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
                    // javaCode.append(String.format("currentX = %f; currentY = %f;\n", currentX, currentY));
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
                    // javaCode.append(String.format("currentX = %f; currentY = %f;\n", currentX, currentY));
                    break;

                case "v": // Vertical Line (relative)
                    double desy = currentY + Double.parseDouble(values[0]);
                    javaCode.append(String.format("bresenhamLine(g2d, %f, %f, "
                            + "%f, %f);\n",
                            currentX, currentY,
                            currentX, desy));
                    currentY += Double.parseDouble(values[0]);
                    // javaCode.append(String.format("currentY = %f;\n", currentY));
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
        String svgPath = 
        "M 366.27 228.72 c 1.94 -0.42 4.44 -1.13 7.2 -2.38 c 1.6 -0.72 3.9 -1.78 6.38 -3.71 c 4.03 -3.15 5.14 -5.96 5.9 -5.62 c 0.97 0.43 0.18 5.32 -1.81 9.43 c -1.47 3.04 -3.3 5.04 -4.95 6.86 c -2.04 2.23 -3.62 3.97 -6.29 5.5 c -2.5 1.44 -4.92 2.09 -6.75 2.4 c -1.12 0.19 -2.02 0.25 -2.59 0.29 c -5.16 0.33 -9.17 -1.2 -11.18 -2.14 l 14.07 -10.62 Z M 365.97 241.19 c 1.19 0.86 3.97 3.13 5.41 7.15 c 0.43 1.2 1.37 3.94 0.67 7.24 c -1.2 5.61 -6.21 8.32 -7.05 8.76 v -20.19 l 0.97 -2.96 Z M 300 245.67 c -12.56 3.57 -25.03 1.24 -25.1 0 c -0.02 -0.4 1.26 -0.47 4.95 -2 c 1.63 -0.68 5.04 -2.11 8.48 -4.38 c 4.64 -3.07 4.45 -4.45 7.43 -5.33 c 2.3 -0.68 4.24 -0.41 7.14 0 c 4.68 0.66 5.28 1.87 8.86 2 c 4.16 0.15 6.52 -1.38 6.73 -0.94 c 0.28 0.56 -3.19 3.73 -8.76 6.7 c -2.79 1.48 -6.1 2.92 -9.74 3.95 Z M 305.29 251.57 c 0.03 -5.26 3.27 -10.18 4.45 -9.86 c 0.95 0.26 0.12 3.82 0.6 11.05 c 0.31 4.61 0.99 8.36 1.52 10.81 c -1.2 -0.8 -3.77 -2.76 -5.33 -6.29 c -1.2 -2.7 -1.24 -5.04 -1.24 -5.71 Z M 312.62 271.95 c 1.94 -0.63 4.83 -1.33 8.38 -1.33 c 3.6 0 6.53 0.7 8.48 1.33 M 349.95 272.9 c 1.81 -0.57 4.84 -1.27 8.57 -0.95 c 3.04 0.26 5.46 1.11 7.05 1.81 M 324.9 291.19 c 0.17 -0.62 0.73 -2.95 -0.48 -5.43 c -0.88 -1.8 -2.24 -2.76 -2.86 -3.14 c -6.67 0.22 -13.33 0.44 -20 0.67";        
        // แปลง SVG path string เป็น Java code
        String javaCode = parseSVGPathToJavaCode(svgPath);

        // แสดงผลลัพธ์ที่ได้
        System.out.println("Generated Java Code for SVG path:");
        System.out.println(javaCode);
    }
}
