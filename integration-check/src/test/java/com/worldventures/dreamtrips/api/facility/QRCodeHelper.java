package com.worldventures.dreamtrips.api.facility;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;

import javax.imageio.ImageIO;


//http://www.programcreek.com/java-api-examples/index.php?api=com.google.zxing.qrcode.QRCodeReader
public class QRCodeHelper {

    public static String readQRCodeFromURLWithAssertion(String url) throws AssertionError {
        try {
            return QRCodeHelper.readQRCodeFromURL(url);
        } catch (IOException e) {
            throw new AssertionError("Bad connection or malformed url at " + url, e);
        } catch (NotFoundException e) {
            throw new AssertionError("Link doesn't contain qr-code at " + url, e);
        } catch (ChecksumException | FormatException e) {
            throw new AssertionError("Bad QrCode image at " + url, e);
        } catch (Throwable throwable) {
            throw new AssertionError("Something wrong with QrCode image at " + url);
        }
    }

    public static String readQRCodeFromURL(String url) throws IOException, FormatException, ChecksumException, NotFoundException {
        BufferedImage image = ImageIO.read(new URL(url));
        return readQRCode(image);
    }

    public static String readQRCode(BufferedImage qrcodeImage) throws FormatException, ChecksumException, NotFoundException {
        Hashtable<DecodeHintType, Object> hintMap = new Hashtable<DecodeHintType, Object>();
        hintMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(qrcodeImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        QRCodeReader reader = new QRCodeReader();
        Result result;

        result = reader.decode(bitmap, hintMap);

        return result.getText();
    }

    public static File createQRCode(String info) {
        return QRCode.from(info).file();
    }

    public static File createSizedQRCode(String info, int height, int width, ImageType imageType) {
        return QRCode.from(info).withSize(width, height).to(imageType).file();
    }

}
