package projekti.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
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
import projekti.entities.Photo;
import projekti.entities.PhotoAlbum;
import projekti.entities.PhotoComment;
import projekti.repositories.PhotoAlbumRepository;
import projekti.repositories.PhotoCommentRepository;
import projekti.repositories.PhotoRepository;

@Controller
public class PhotoAlbumController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;

    @Autowired
    private PhotoCommentRepository photoCommentRepository;

    @GetMapping("/users/{username}/photos")
    public String photos(Model model, @PathVariable String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        model.addAttribute("currentuser", accountService.loadByUsername(currentUser));

        if (photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)) != null) {
            model.addAttribute("albumPhotos", photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)).getPhotos());
        }

        if (accountService.loadByUsername(username).getProfilePhoto() != null) {
            model.addAttribute("profilePhoto", accountService.loadByUsername(username).getProfilePhoto().getId());
        }

        if (username.equals(currentUser)) {
            return "myphotos";
        }
        
        model.addAttribute("user", accountService.loadByUsername(username));
        return "photos";
    }

    @GetMapping("/users/{username}/photos/{id}/view_photo")
    public String viewPhoto(Model model, @PathVariable String username, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        model.addAttribute("currentuser", accountService.loadByUsername(currentUser));
        model.addAttribute("user", accountService.loadByUsername(username));
        model.addAttribute("comments", photoRepository.getOne(id).getComments());

        if (photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)) != null) {
            model.addAttribute("photo", photoRepository.getOne(id));
        }

        if (accountService.loadByUsername(username).getProfilePhoto() != null) {
            model.addAttribute("profilePhoto", accountService.loadByUsername(username).getProfilePhoto().getId());
        }

        return "viewphoto";
    }

    @GetMapping(path = "/users/{username}/photos/{id}", produces = "image/jpeg")
    @ResponseBody
    public byte[] getPhoto(@PathVariable Long id) {
        if (photoRepository.existsById(id)) {
            return photoRepository.getOne(id).getContent();
        }

        return null;
    }

    @Transactional
    @PostMapping("/users/{username}/photos/add_photo")
    public String save(@PathVariable String username, @RequestParam("file") MultipartFile file, @RequestParam String description,
            @RequestParam(defaultValue = "false") boolean isProfilePhoto) throws IOException {
        if (!file.getContentType().equals("image/jpeg")
                || photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)).getPhotos().size() == 10) {
            return "redirect:/users/" + username + "/photos";
        }

        Photo fo = new Photo();
        fo.setContent(file.getBytes());
        fo.setDescription(description);
        photoRepository.save(fo);

        if (photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)) != null) {
            PhotoAlbum album = photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username));
            album.getPhotos().add(fo);
        } else {
            PhotoAlbum album = new PhotoAlbum();
            album.setUser(accountService.loadByUsername(username));
            photoAlbumRepository.save(album);
        }

        if (isProfilePhoto) {
            accountService.loadByUsername(username).setProfilePhoto(fo);
        }

        return "redirect:/users/" + username + "/photos";
    }

    @Transactional
    @PostMapping("/users/{username}/photos/{id}/delete_photo")
    public String deletePhoto(@PathVariable String username, @PathVariable Long id) {
        if (accountService.loadByUsername(username).getProfilePhoto() != null
                && accountService.loadByUsername(username).getProfilePhoto().getId() == id) {
            accountService.loadByUsername(username).setProfilePhoto(null);
        }

        photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByUsername(username)).getPhotos().remove(photoRepository.getOne(id));
        photoRepository.deleteById(id);

        return "redirect:/users/" + username + "/photos";
    }

    @Transactional
    @PostMapping("users/{username}/photos/{id}/post_comment")
    public String commentPhoto(@PathVariable String username, @PathVariable Long id, @RequestParam String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        PhotoComment photoComment = new PhotoComment();
        photoComment.setSender(accountService.loadByUsername(currentUser));
        photoComment.setContent(content);
        photoComment.setDateTime(LocalDateTime.now());
        photoComment.setPhoto(photoRepository.getOne(id));
        photoCommentRepository.save(photoComment);

        photoRepository.getOne(id).getComments().add(photoComment);

        return "redirect:/users/" + username + "/photos/" + id + "/view_photo";
    }
    
    @Transactional
    @PostMapping("/users/{username}/photos/{id}/like_photo")
    public String likePhoto(@PathVariable String username, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        
        photoRepository.getOne(id).getLikes().add(accountService.loadByUsername(currentUser));

        return "redirect:/users/" + username + "/photos/";
    }

}
