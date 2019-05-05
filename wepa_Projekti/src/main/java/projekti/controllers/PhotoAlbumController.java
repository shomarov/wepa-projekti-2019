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

    @GetMapping("/users/{profileLink}/photos")
    public String photos(Model model, @PathVariable String profileLink) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        model.addAttribute("currentuser", accountService.loadByUsername(currentUser));

        if (photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByProfileLink(profileLink)) != null) {
            model.addAttribute("albumPhotos", photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByProfileLink(profileLink)).getPhotos());
        }

        if (accountService.loadByProfileLink(profileLink).getProfilePhoto() != null) {
            model.addAttribute("profilePhoto", accountService.loadByProfileLink(profileLink).getProfilePhoto().getId());
        }

        if (accountService.loadByProfileLink(profileLink).getUsername().equals(currentUser)) {
            return "myphotos";
        }
        
        model.addAttribute("user", accountService.loadByProfileLink(profileLink));
        return "photos";
    }

    @GetMapping("/users/{profileLink}/photos/{id}/view_photo")
    public String viewPhoto(Model model, @PathVariable String profileLink, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        model.addAttribute("currentuser", accountService.loadByUsername(currentUser));
        model.addAttribute("user", accountService.loadByProfileLink(profileLink));
        model.addAttribute("comments", photoRepository.getOne(id).getComments());

        if (photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByProfileLink(profileLink)) != null) {
            model.addAttribute("photo", photoRepository.getOne(id));
        }

        if (accountService.loadByProfileLink(profileLink).getProfilePhoto() != null) {
            model.addAttribute("profilePhoto", accountService.loadByProfileLink(profileLink).getProfilePhoto().getId());
        }

        return "viewphoto";
    }

    @GetMapping(path = "/users/{profileLink}/photos/{id}", produces = "image/jpeg")
    @ResponseBody
    public byte[] getPhoto(@PathVariable Long id) {
        if (photoRepository.existsById(id)) {
            return photoRepository.getOne(id).getContent();
        }

        return null;
    }

    @Transactional
    @PostMapping("/users/{profileLink}/photos/add_photo")
    public String save(@PathVariable String profileLink, @RequestParam("file") MultipartFile file, @RequestParam String description,
            @RequestParam(defaultValue = "false") boolean isProfilePhoto) throws IOException {
        if (!file.getContentType().equals("image/jpeg")
                || photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByProfileLink(profileLink)).getPhotos().size() == 10) {
            return "redirect:/users/" + profileLink + "/photos";
        }

        Photo fo = new Photo();
        fo.setContent(file.getBytes());
        fo.setDescription(description);
        photoRepository.save(fo);

        if (photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByProfileLink(profileLink)) != null) {
            PhotoAlbum album = photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByProfileLink(profileLink));
            album.getPhotos().add(fo);
        } else {
            PhotoAlbum album = new PhotoAlbum();
            album.setUser(accountService.loadByProfileLink(profileLink));
            photoAlbumRepository.save(album);
        }

        if (isProfilePhoto) {
            accountService.loadByProfileLink(profileLink).setProfilePhoto(fo);
        }

        return "redirect:/users/" + profileLink + "/photos";
    }
    
    @Transactional
    @PostMapping("/users/{profileLink}/photos/{id}/set_profile_photo")
    public String setProfilePhoto(@PathVariable String profileLink, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        
        accountService.loadByUsername(currentUser).setProfilePhoto(photoRepository.getOne(id));
        
        return "redirect:/users/" + profileLink + "/photos";
    }

    @Transactional
    @PostMapping("/users/{profileLink}/photos/{id}/delete_photo")
    public String deletePhoto(@PathVariable String profileLink, @PathVariable Long id) {
        if (accountService.loadByProfileLink(profileLink).getProfilePhoto() != null
                && accountService.loadByProfileLink(profileLink).getProfilePhoto().getId() == id) {
            accountService.loadByProfileLink(profileLink).setProfilePhoto(null);
        }
        photoAlbumRepository.findPhotoAlbumByUser(accountService.loadByProfileLink(profileLink)).getPhotos().remove(photoRepository.getOne(id));
        photoRepository.deleteById(id);

        return "redirect:/users/" + profileLink + "/photos";
    }

    @Transactional
    @PostMapping("users/{profileLink}/photos/{id}/post_comment")
    public String commentPhoto(@PathVariable String profileLink, @PathVariable Long id, @RequestParam String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        PhotoComment photoComment = new PhotoComment();
        photoComment.setSender(accountService.loadByUsername(currentUser));
        photoComment.setContent(content);
        photoComment.setDateTime(LocalDateTime.now());
        photoComment.setPhoto(photoRepository.getOne(id));
        photoCommentRepository.save(photoComment);

        photoRepository.getOne(id).getComments().add(photoComment);

        return "redirect:/users/" + profileLink + "/photos/" + id + "/view_photo";
    }
    
    @Transactional
    @PostMapping("/users/{profileLink}/photos/{id}/like_photo")
    public String likePhoto(@PathVariable String profileLink, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        
        photoRepository.getOne(id).getLikes().add(accountService.loadByUsername(currentUser));

        return "redirect:/users/" + profileLink + "/photos/";
    }

}
