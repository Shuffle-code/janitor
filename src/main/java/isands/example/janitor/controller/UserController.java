package isands.example.janitor.controller;


import isands.example.janitor.entity.Janitor;
import isands.example.janitor.entity.security.AccountUser;
import isands.example.janitor.service.JanitorImageService;
import isands.example.janitor.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JanitorImageService janitorImageService;
    @GetMapping
    public String userPage(Model model, Principal principal) {
        Janitor janitor;
        AccountUser accountUser = userService.findByUsername(principal.getName());
        janitor = accountUser.getJanitor();
        model.addAttribute("accountUser", accountUser);
        List<Long> imagesId = new ArrayList<>(janitorImageService.uploadMultipleFiles(janitor.getId()));
        model.addAttribute("playerImagesId", imagesId);
        model.addAttribute("player", janitor);
        return "auth/user-info";
    }

    @GetMapping(value = "/image/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('janitor.read') || isAnonymous()")
    public byte[] getImage(@PathVariable Long id) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(janitorImageService.loadFileAsImage(id), "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

}
