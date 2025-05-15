package pl.ecommerce.project.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.ecommerce.project.exception.APIException;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file, String folder) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "auto"      // Automatyczne rozpoznanie typu pliku
                ));
        return (String) uploadResult.get("secure_url"); // Zwraca URL do pliku
    }

    private void validateImageFile(MultipartFile image) {
        if (image.isEmpty()) {
            throw new APIException("Plik jest pusty");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new APIException("Nieprawidłowy typ pliku");
        }

        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg")) {
            throw new APIException("Dozwolone typy plików: JPEG, PNG, JPEG");
        }

        long maxSize = 5 * 1024 * 1024; // 5 MB
        if (image.getSize() > maxSize) {
            throw new APIException("Plik jest za duży. Maksymalny rozmiar to 5 MB");
        }
    }
}

