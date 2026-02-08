package david_mirz.contactapp.resource;

import static david_mirz.contactapp.constants.Constants.PHOTO_DIRECTORY;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import david_mirz.contactapp.domain.Contact;
import david_mirz.contactapp.service.ContactService;
import lombok.RequiredArgsConstructor;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactResource {

    private final ContactService service;

    @PostMapping
    public ResponseEntity<Contact> createContact (@RequestBody Contact contact) {
        return ResponseEntity.created(URI.create("/contacts/userID")).body(service.createContact(contact));
    }

    @GetMapping
    public ResponseEntity<Page<Contact>> getContacts(@RequestParam (value="page", defaultValue = "0") int page, @RequestParam (value="size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(service.getAllContacts(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContact (@PathVariable(value="id") String id){
        return ResponseEntity.ok().body(service.getContact(id));
    }

    @PutMapping("/photo")
    public ResponseEntity<String> uploadPhoto (@RequestParam(value="id") String id, @RequestParam(value = "file") MultipartFile file){
        return ResponseEntity.ok().body(service.uploadPhoto(id, file));
    }

    @GetMapping(path="/image/{filename}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public byte[] getPhoto(@PathVariable(value = "filename") String filename) throws IOException{
        return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable String id){
        service.deleteContact(id);
        return ResponseEntity.ok().body(null);
    }
}
