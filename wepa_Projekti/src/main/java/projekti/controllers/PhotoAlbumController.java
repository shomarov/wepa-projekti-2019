package projekti.controllers;

import java.io.IOException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import projekti.services.AccountService;
import projekti.entities.FileObject;
import projekti.entities.PhotoAlbum;
import projekti.repositories.FileObjectRepository;
import projekti.repositories.PhotoAlbumRepository;

@Controller
public class PhotoAlbumController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private FileObjectRepository fileObjectRepository;

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;

    @GetMapping("/users/{username}/photos")
    public String photos(Model model, @PathVariable String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        model.addAttribute("currentuser", accountService.loadByUsername(currentUser));
        model.addAttribute("user", accountService.loadByUsername(username));
        
        if (photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)) != null) {
            model.addAttribute("albumPhotos", photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)).getPhotos());
        }
        
        if (photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)).getProfilePhoto() != null) {
            model.addAttribute("profilePhoto", photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)).getProfilePhoto().getId());
        }

        return "photos";
    }

    @GetMapping(path = "/users/{username}/photos/{id}", produces = "image/gif")
    @ResponseBody
    public byte[] getGif(@PathVariable Long id) {
        if (fileObjectRepository.existsById(id)) {
            return fileObjectRepository.getOne(id).getContent();
        }
        
        return null;
    }

    @Transactional
    @PostMapping("users/{username}/photos/add_photo")
    public String save(@PathVariable String username, @RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "false") boolean isProfilePhoto) throws IOException {
        if (!file.getContentType().equals("image/jpeg")
                || photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)).getPhotos().size() == 10) {
            return "redirect:/users/" + username + "/photos";
        }
        
        FileObject fo = new FileObject();
        fo.setContent(file.getBytes());
        fileObjectRepository.save(fo);

        if (photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)) != null) {
            PhotoAlbum album = photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username));
            album.getPhotos().add(fo);
        } else {
            PhotoAlbum album = new PhotoAlbum();
            album.setUser(accountService.loadByUsername(username));
            photoAlbumRepository.save(album);
        }

        if (isProfilePhoto) {
            photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)).setProfilePhoto(fo);
        }

        return "redirect:/users/" + username + "/photos";
    }

}
