package org.example;

import io.github.humbleui.skija.*;
import io.github.humbleui.skija.paragraph.*;
import io.github.humbleui.skija.svg.SVGDOM;
import io.github.humbleui.types.Rect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    private static final int CANVAS_SIZE = 512;

    public static void main(String[] args) throws IOException {
        String dirPath = null;
        int loopCount = 1;
        boolean doAll = true;
        boolean drawPath = false;
        boolean drawRaster = false;
        boolean drawText = false;
        boolean drawSvg = false;
        boolean save = false;
        int scale = 1;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--dir" -> dirPath = args[++i];
                case "--loop" -> {
                    try {
                        loopCount = Integer.parseInt(args[++i]);
                    } catch (Exception ex) {
                        System.err.println("Set loop count with --loop <number>");
                        System.exit(1);
                    }
                }
                case "--path" -> {
                    doAll = false;
                    drawPath = true;
                }
                case "--raster" -> {
                    doAll = false;
                    drawRaster = true;
                }
                case "--text" -> {
                    doAll = false;
                    drawText = true;
                }
                case "--svg" -> {
                    doAll = false;
                    drawSvg = true;
                }
                case "--save" -> {
                    doAll = false;
                    save = true;
                }
                case "--scale" -> {
                    try {
                        scale = Integer.parseInt(args[++i]);
                    } catch (Exception ex) {
                        System.err.println("Set scale with --scale <number>");
                        System.exit(1);
                    }
                }
                default -> {
                    System.err.printf("Invalid argument: %s\n", arg);
                    System.exit(1);
                }
            }
        }

        if (dirPath == null) {
            System.err.println("Set working directory with --dir <path>");
            System.exit(1);
        }

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.printf("No such directory: %s\n", dirPath);
            System.exit(1);
        }

        if (doAll) {
            drawPath = true;
            drawRaster = true;
            drawText = true;
            drawSvg = true;
            save = true;
        }
        for (int i = 0; i < loopCount; i++) {
            performance_test(dirPath, drawPath, drawRaster, drawText, drawSvg, save, scale);
        }
    }

    private static void performance_test(String workingPath, boolean drawPath, boolean drawRaster, boolean drawText, boolean drawSvg, boolean save, int scale) throws IOException {
        try (var surface = Surface.makeRaster(ImageInfo.makeN32Premul(CANVAS_SIZE * scale, CANVAS_SIZE * scale)); var paint = new Paint()) {
            paint.setAntiAlias(true);
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.scale(scale, scale);
            if (drawPath) {
                File pathFile = checkFileExists(workingPath + File.separator + "path.txt");
                drawPath(canvas, paint, pathFile);
            }
            if (drawRaster) {
                File rasterFile = checkFileExists(workingPath + File.separator + "mars.jpg");
                drawRaster(canvas, paint, rasterFile);
            }
            if (drawText) {
                File fontFile = checkFileExists(workingPath + File.separator + "Adigiana_Ultra.ttf");
                drawText(canvas, fontFile);
            }
            if (drawSvg) {
                File svgFile = checkFileExists(workingPath + File.separator + "pinocchio.svg");
                drawSVG(canvas, svgFile);
            }
            if (save) {
                File outputFile = new File(workingPath + File.separator + "output-java.png");
                saveToPng(surface, outputFile);
            }
        }
    }

    private static File checkFileExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.err.printf("File not found: %s\n", file);
            System.exit(1);
        }
        return file;
    }

    private static void drawPath(Canvas canvas, Paint paint, File pathFile) throws IOException {
        paint.setColor(0xFF000000);
        canvas.save();
        canvas.translate(12.0f, 12.0f);
        canvas.scale(0.45f, 0.45f);
        var path = Files.readString(pathFile.toPath());
        canvas.drawPath(Path.makeFromSVGString(path), paint);
        canvas.restore();
    }

    private static void drawRaster(Canvas canvas, Paint paint, File rasterFile) {
        canvas.save();
        canvas.translate(250, 0.0f);
        canvas.scale(0.05f, 0.05f);
        try (var bitmapData = Data.makeFromFileName(rasterFile.getAbsolutePath())) {
            var bitmap = Image.makeDeferredFromEncodedBytes(bitmapData.getBytes());
            var rect = Rect.makeWH(bitmap.getWidth(), bitmap.getHeight());
            canvas.drawImageRect(bitmap, rect, rect, new FilterMipmap(FilterMode.LINEAR, MipmapMode.LINEAR), paint, false);
        }
        canvas.restore();
    }

    private static void drawText(Canvas canvas, File fontFile) {
        var typefaceProvider = new TypefaceFontProvider();
        try (var font = Typeface.makeFromFile(fontFile.getAbsolutePath())) {
            typefaceProvider.registerTypeface(font, "Adigiana");
        }
        var fontCollection = new FontCollection();
        fontCollection.setAssetFontManager(typefaceProvider);

        var style = new ParagraphStyle();
        var textStyle = new TextStyle();
        textStyle.setColor(Color.makeRGB(0, 0, 0));
        textStyle.setFontSize(15.0f);
        textStyle.setFontFamilies(new String[]{"Adigiana"});
        style.setTextStyle(textStyle);
        var paragraphBuilder = new ParagraphBuilder(style, fontCollection);
        paragraphBuilder.addText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, ");
        textStyle.setColor(Color.makeRGB(255, 0, 0));
        paragraphBuilder.pushStyle(textStyle);
        paragraphBuilder.addText("sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ");
        textStyle.setColor(Color.makeRGB(0, 255, 0));
        paragraphBuilder.pushStyle(textStyle);
        paragraphBuilder.addText("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut ");
        textStyle.setColor(Color.makeRGB(0, 0, 255));
        paragraphBuilder.pushStyle(textStyle);
        paragraphBuilder.addText("aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in ");
        textStyle.setColor(Color.makeRGB(255, 255, 0));
        paragraphBuilder.pushStyle(textStyle);
        paragraphBuilder.addText("voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint ");
        textStyle.setColor(Color.makeRGB(0, 255, 255));
        paragraphBuilder.pushStyle(textStyle);
        paragraphBuilder.addText("occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n");

        var paragraph = paragraphBuilder.build();
        paragraph.layout(225);

        paragraph.paint(canvas, 25.0f, 275.0f);
    }

    private static void drawSVG(Canvas canvas, File svgFile) {
        canvas.save();
        canvas.translate(350.0f, 275.0f);
        canvas.scale(0.22f, 0.22f);
        try (var svgData = Data.makeFromFileName(svgFile.getAbsolutePath())) {
            var svg = new SVGDOM(svgData);
            svg.render(canvas);
        }
        canvas.restore();
    }

    private static void saveToPng(Surface surface, File outputFile) throws IOException {
        var data = EncoderPNG.encode(surface.makeImageSnapshot());
        if (data != null) {
            byte[] pngBytes = data.getBytes();
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(pngBytes);
            }
        }
    }
}