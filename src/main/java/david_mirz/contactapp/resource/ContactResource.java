package david_mirz.contactapp.resource;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import david_mirz.contactapp.domain.Contact;
import david_mirz.contactapp.service.ContactService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/contacts/")
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

}
