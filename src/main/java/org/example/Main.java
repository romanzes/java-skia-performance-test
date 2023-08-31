package org.example;

import io.github.humbleui.skija.*;
import io.github.humbleui.skija.paragraph.*;
import io.github.humbleui.skija.svg.SVGDOM;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        try (var surface = Surface.makeRasterN32Premul(2048, 2048); var paint = new Paint()) {
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            drawPath(canvas, paint);
            drawRaster(canvas);
            drawText(canvas);
            drawSVG(canvas);
            saveToPng(surface);
        }
    }

    private static void drawPath(Canvas canvas, Paint paint) {
        paint.setColor(0xFF000000);
        Path path = Path.makeFromSVGString("M437.02,74.981c48.352,48.352,74.98,112.64,74.98,181.02s-26.629,132.667-74.98,181.019C388.667,485.371,324.38,512,256,512\n" +
                "\ts-132.667-26.629-181.02-74.98C26.629,388.668,0,324.381,0,256.001s26.627-132.668,74.98-181.02S187.62,0,256,0\n" +
                "\tS388.667,26.629,437.02,74.981z M414.392,414.393c31.529-31.529,52.493-70.804,61.137-113.531\n" +
                "\tc-6.737,9.918-13.182,13.598-17.172-8.603c-4.11-36.195-37.354-13.073-58.259-25.93c-22.002,14.829-71.453-28.831-63.049,20.412\n" +
                "\tc12.967,22.211,70.004-29.726,41.574,17.271c-18.137,32.809-66.321,105.466-60.053,143.129c0.791,54.872-56.067,11.442-75.657-6.76\n" +
                "\tc-13.178-36.46-4.491-100.188-38.949-118.043c-37.401-1.624-69.502-5.023-83.997-46.835c-8.723-29.914,9.282-74.447,41.339-81.322\n" +
                "\tc46.925-29.483,63.687,34.527,107.695,35.717c13.664-14.297,50.908-18.843,53.996-34.875c-28.875-5.095,36.634-24.279-2.764-35.191\n" +
                "\tc-21.735,2.556-35.739,22.537-24.185,39.479c-42.119,9.821-43.468-60.952-83.955-38.629c-1.029,35.295-66.111,11.443-22.518,4.286\n" +
                "\tc14.978-6.544-24.43-25.508-3.14-22.062c10.458-0.568,45.666-12.906,36.138-21.201c19.605-12.17,36.08,29.145,55.269-0.941\n" +
                "\tc13.854-23.133-5.81-27.404-23.175-15.678c-9.79-10.962,17.285-34.638,41.166-44.869c7.959-3.41,15.561-5.268,21.373-4.742\n" +
                "\tc12.029,13.896,34.275,16.303,35.439-1.671C322.855,39.537,290.008,32,256,32c-48.811,0-95.235,15.512-133.654,44.195\n" +
                "\tc10.325,4.73,16.186,10.619,6.239,18.148c-7.728,23.027-39.085,53.938-66.612,49.562c-14.293,24.648-23.706,51.803-27.73,80.264\n" +
                "\tc23.056,7.628,28.372,22.725,23.418,27.775c-11.748,10.244-18.968,24.765-22.688,40.662c7.505,45.918,29.086,88.237,62.635,121.787\n" +
                "\tC139.916,456.7,196.167,480,256,480C315.832,480,372.084,456.7,414.392,414.393z");
        canvas.save();
        canvas.translate(100.0f, 100.0f);
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    private static void drawRaster(Canvas canvas) {
        try (var bitmapData = Data.makeFromFileName("mars.jpg")) {
            var image = Image.makeFromEncoded(bitmapData.getBytes());
            canvas.save();
            canvas.translate(1000.0f, 0.0f);
            canvas.scale(0.2f, 0.2f);
            canvas.drawImage(image, 0.0f, 0.0f);
            canvas.restore();
        }
    }

    private static void drawText(Canvas canvas) {
        try (
                var style = new ParagraphStyle();
                var textStyle = new TextStyle();
                var typefaceProvider = new TypefaceFontProvider();
                var font = Typeface.makeFromFile("Adigiana_Ultra.ttf");
                var fontCollection = new FontCollection()
        ) {
            textStyle.setColor(Color.makeRGB(0, 0, 0));
            textStyle.setFontSize(60.0f);
            textStyle.setFontFamilies(new String[]{"Adigiana"});
            style.setTextStyle(textStyle);
            typefaceProvider.registerTypeface(font, "Adigiana");
            fontCollection.setAssetFontManager(typefaceProvider);
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
            paragraph.layout(900);

            paragraph.paint(canvas, 100.0f, 1100.0f);
        }
    }

    private static void drawSVG(Canvas canvas) {
        try (var bitmapData = Data.makeFromFileName("pinocchio.svg")) {
            var svg = new SVGDOM(bitmapData);
            canvas.save();
            canvas.translate(1200.0f, 1200.0f);
            canvas.scale(0.5f, 0.5f);
            svg.render(canvas);
            canvas.restore();
        }
    }

    private static void saveToPng(Surface surface) throws IOException {
        var data = surface.makeImageSnapshot().encodeToData();
        if (data != null) {
            byte[] pngBytes = data.getBytes();
            java.nio.file.Files.write(java.nio.file.Path.of("output.png"), pngBytes);
        }
    }

    private static void saveToPdf() {

    }
}