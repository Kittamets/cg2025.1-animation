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
        String svgPath = 
            "M 310.81 292.49 c 1.65 0.76 4.47 1.7 7.96 1.11 c 3.07 -0.52 5.44 -2.03 6.9 -3.17 M 352.2 284.24 h 15.28 c -0.34 1.89 -1.02 4.36 -2.48 6.95 c -0.55 0.98 -1.14 1.85 -1.71 2.59 M 353.38 283.76 c -0.33 1.15 -0.83 2.55 -1.62 4.05 c -0.72 1.37 -1.49 2.5 -2.19 3.38 M 352.2 292.97 c 1.39 0.46 3.28 0.94 5.56 1.08 c 2.63 0.16 4.83 -0.18 6.38 -0.54 M 353.38 284.71 c 0.22 1.06 0.56 2.54 1.05 4.3 c 0.95 3.42 1.41 4.1 2.1 4.52 c 0.96 0.58 2.39 0.68 3.31 0 c 1.04 -0.76 1.05 -2.22 1.07 -3.77 c 0.01 -1.1 -0.13 -2.91 -1.07 -5.05 h -6.46 Z M 286.33 225.23 l -12.1 10.15 c 1.39 -0.67 3.45 -1.46 6.05 -1.84 c 2.47 -0.36 4.56 -0.22 6.05 0 M 277.86 261.95 c -1.59 2.27 -3.6 4.82 -6.1 7.43 c -2.39 2.5 -4.74 4.54 -6.86 6.19 c 2.48 -0.2 6.03 -0.76 10 -2.38 c 3.94 -1.6 6.86 -3.66 8.76 -5.24 M 305.29 245.1 c -2.07 3.58 -3.9 6.52 -5.29 8.67 c -2.8 4.35 -4.19 6.52 -5.76 8.19 c -3.3 3.52 -6.47 5.27 -6.1 6 c 0.39 0.77 4.1 -0.99 4.76 0 c 0.67 1 -2.68 3.38 -6.57 9.33 c -2.46 3.76 -3.98 6.15 -4.38 9.62 c -0.07 0.58 -0.25 2.45 -0.48 2.45 c -0.32 0 0.31 -3.71 -1.71 -5.59 c -1.49 -1.39 -3.79 -1.21 -4.57 -1.14 c -0.74 0.06 -2.52 0.32 -4.1 1.62 c -2 1.65 -2.43 3.95 -2.86 6.19 c -0.5 2.63 -0.28 4.65 0 7.14 c 0.35 3.19 1.05 5.67 1.52 7.14 c 0.71 2.19 1.5 4.59 3.33 7.24 c 1.46 2.12 2.51 3.63 4.57 4.38 c 0.44 0.16 2.89 1 5.33 0 c 1.71 -0.7 2.1 -1.76 3.33 -1.71 c 0.71 0.03 1.58 0.42 2.95 2.29 c 2.28 3.12 1.99 4.97 3.52 7.9 c 1.23 2.36 2.75 3.73 4.86 5.62 c 0.85 0.76 2.29 1.97 7.62 5.05 c 4.56 2.64 4.78 2.49 9.24 5.05 c 4.82 2.76 7.22 4.14 8.38 5.05 c 1.13 0.88 3.04 2.5 6.1 3.71 c 0.95 0.38 1.75 0.62 2.29 0.76 c 1.03 -1.03 2.74 -2.59 5.14 -4.1 c 2.51 -1.57 3.38 -1.58 6.29 -3.33 c 1.87 -1.13 3.22 -2.18 5.9 -4.29 c 2.31 -1.81 2.43 -2.03 6.1 -5.05 c 3.97 -3.27 4.52 -3.58 6.76 -5.52 c 2.4 -2.08 4.26 -3.88 5.43 -5.05 c -0.08 -1.93 -0.15 -4.95 0 -8.67 c 0.17 -4.16 0.4 -4.07 0.57 -8.57 c 0.15 -4.06 0.02 -5.71 0.48 -9.24 c 0.14 -1.1 0.31 -2.41 0.7 -4.13 c 0.86 -3.82 1.65 -4.69 2.35 -7.39 c 0.37 -1.43 0.21 -1.44 0.57 -8.57 c 0.28 -5.57 0.38 -5.53 0.48 -8.57 c 0.07 -2.19 0.11 -5.25 0 -8.95 M 347.1 214.9 c -0.21 -11.78 0.19 -14.72 0.67 -14.76 c 0.51 -0.04 0.64 3.31 3.43 6.76 c 1.17 1.44 2.46 2.48 3.62 3.41 c 1.01 0.81 2.41 1.92 4.48 2.97 c 1.64 0.83 3.12 1.32 4.19 1.62 M 377 236.24 c 0.58 1.07 1.34 2.76 1.71 4.95 c 0.06 0.34 0.82 4.97 -0.38 5.43 c -1.31 0.5 -4.27 -4.2 -4.29 -4.19 c -0.08 0.07 4.67 6.29 4.67 6.29 c 1.42 1.86 1.99 2.61 2.48 3.92 c 0.46 1.26 0.27 1.5 0.95 4.13 c 0.48 1.86 0.73 2.81 1.33 3.95 c 0.57 1.08 1.16 1.81 2 2.86 c 1.78 2.2 2.73 2.76 2.57 3.05 c -0.23 0.42 -2.59 -0.37 -9.24 -3.05 c -2.72 -1.1 -4.97 -2.02 -6.51 -2.67 M 380.18 264.33 c -0.3 1.07 -0.61 2.59 -0.61 4.43 c 0 0.88 0 2.6 0.61 4.52 c 0.83 2.64 1.99 3.17 3.48 5.9 c 0.65 1.2 1.48 3.03 2 5.52 c -2.56 -1.46 -5.13 -2.92 -7.71 -4.38 c -1.97 -1.12 -3.94 -2.23 -5.9 -3.33";

        // แปลง SVG path string เป็น Java code
        String javaCode = parseSVGPathToJavaCode(svgPath);

        // แสดงผลลัพธ์ที่ได้
        System.out.println("Generated Java Code for SVG path:");
        System.out.println(javaCode);
    }
}
