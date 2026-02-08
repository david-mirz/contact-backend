package david_mirz.contactapp.service;

import java.nio.file.CopyOption;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import david_mirz.contactapp.domain.Contact;
import static david_mirz.contactapp.constants.Constants.PHOTO_DIRECTORY;
import david_mirz.contactapp.repo.ContactRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ContactService {
   


    private final ContactRepo repo;

    public Page<Contact> getAllContacts(int page, int size){
        return repo.findAll(PageRequest.of(page,size,Sort.by("name")));
    }

    public Contact getContact(String id){
        return repo.findById(id).orElseThrow(()-> new RuntimeException("Contact not found"));
    }

    public Contact createContact(Contact contact){
        return repo.save(contact);
    }
    
    public void deleteContact(String id){
        Contact contact = getContact(id);
        repo.delete(contact);
    }

    public String uploadPhoto(String id, MultipartFile file){
        Contact contact = getContact(id);
        String photoUrl = photoFunction.apply(id, file);
        contact.setPhotoUrl(photoUrl);
        repo.save(contact);
        return photoUrl;
    }

    private Function<String, String> fileExtention = filename -> 
        Optional.of(filename)
        .filter(name -> name.contains("."))
        .map(name -> "." + name.substring(filename.lastIndexOf(".")+1))
        .orElse(".png");
    

    private BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String filename = id + fileExtention.apply(image.getOriginalFilename());
        try {
            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if (!Files.exists(fileStorageLocation)) {Files.createDirectories(fileStorageLocation); }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename),REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/contacts/image/" + filename)
                .toUriString();

        } catch (Exception e) {
            throw new RuntimeException("unable to save image");    
        }
        
    };
}
