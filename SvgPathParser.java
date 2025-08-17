import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SvgPathParser {

    /**
     * Converts an SVG path string into equivalent Java drawing commands.
     * The generated code assumes the existence of helper methods like
     * `bresenhamLine` and `bezierCurve` which actually perform the drawing
     * inside paintComponent.
     */
    public static String parseSVGPathToJavaCode(String svgPath) {
        StringBuilder javaCode = new StringBuilder();

        // Track the current "pen" position while parsing the path
        double currentX = 0;
        double currentY = 0;

        // Regex to capture SVG commands:
        // - A command letter (M, l, h, v, c, etc.)
        // - Followed by one or more numeric coordinate values
        Pattern pattern = Pattern.compile(
                "([a-zA-Z])\\s*(-?\\d*\\.?\\d+\\s*-?\\d*\\.?\\d+\\s*(-?\\d*\\.?\\d+\\s*-?\\d*\\.?\\d+\\s*)*)"
        );
        Matcher matcher = pattern.matcher(svgPath);

        // Loop through all recognized SVG commands in the input string
        while (matcher.find()) {
            String command = matcher.group(1);          // SVG command letter
            String[] values = matcher.group(2).trim().split("\\s+"); // Numbers after the command

            switch (command) {
                case "M": // Move To (absolute coordinates)
                    // Sets the new current position without drawing
                    currentX = Double.parseDouble(values[0]);
                    currentY = Double.parseDouble(values[1]);
                    break;

                case "c": // Cubic Bézier Curve (relative coordinates)
                    // Control point 1 (relative to currentX, currentY)
                    double x1 = currentX + Double.parseDouble(values[0]);
                    double y1 = currentY + Double.parseDouble(values[1]);

                    // Control point 2
                    double x2 = currentX + Double.parseDouble(values[2]);
                    double y2 = currentY + Double.parseDouble(values[3]);

                    // End point
                    double x = currentX + Double.parseDouble(values[4]);
                    double y = currentY + Double.parseDouble(values[5]);

                    // Append equivalent Java drawing function call
                    javaCode.append(String.format(
                            "bezierCurve(g2d, %f, %f, %f, %f, %f, %f, %f , %f , 3);\n",
                            currentX, currentY,
                            x1, y1,
                            x2, y2,
                            x, y
                    ));

                    // Update current position to the end of the curve
                    currentX += Double.parseDouble(values[4]);
                    currentY += Double.parseDouble(values[5]);
                    break;

                case "l": // Line To (relative coordinates)
                    // Starting point (current position)
                    double xs = currentX;
                    double ys = currentY;

                    // Destination point (relative offset)
                    double xd = currentX + Double.parseDouble(values[0]);
                    double yd = currentY + Double.parseDouble(values[1]);

                    // Append Java code to draw a line
                    javaCode.append(String.format(
                            "bresenhamLine(g2d, %f, %f, %f, %f);\n",
                            xs, ys, xd, yd
                    ));

                    // Update current position
                    currentX += Double.parseDouble(values[0]);
                    currentY += Double.parseDouble(values[1]);
                    break;

                case "v": // Vertical Line (relative)
                    // Vertical offset
                    double desy = currentY + Double.parseDouble(values[0]);

                    // Append Java line drawing
                    javaCode.append(String.format(
                            "bresenhamLine(g2d, %f, %f, %f, %f);\n",
                            currentX, currentY, currentX, desy
                    ));

                    // Update Y position
                    currentY += Double.parseDouble(values[0]);
                    break;

                case "h": // Horizontal Line (relative)
                    // Horizontal offset
                    double newX = currentX + Double.parseDouble(values[0]);

                    // Append Java line drawing
                    javaCode.append(String.format(
                            "bresenhamLine(g2d, %f, %f, %f, %f);\n",
                            currentX, currentY, newX, currentY
                    ));

                    // Update X position
                    currentX += Double.parseDouble(values[0]);
                    break;

                default:
                    // Any other commands (not implemented) are ignored
                    break;
            }
        }
        // Return the generated Java code as a string
        return javaCode.toString();
    }

    // Simple test for the parser
    public static void main(String[] args) {
        // Example SVG path string (replace with real data)
        // You need to minify your output before use
        // E.g "M 96.55 606.22 h -7.43 c 1.76 -2.62 3.86 -6.06 5.9 -10.26 c 1.06 -2.17 2.48 -5.12 3.81 -9.14"
        String svgPath = "";

        // Convert SVG path to equivalent Java code
        String javaCode = parseSVGPathToJavaCode(svgPath);

        // Print generated Java drawing commands
        System.out.println("Generated Java Code for SVG path:");
        System.out.println(javaCode);
    }
}
