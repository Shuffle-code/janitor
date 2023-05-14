package isands.example.janitor.controller;

import isands.example.janitor.dao.JanitorDao;
import isands.example.janitor.entity.Janitor;
import isands.example.janitor.service.JanitorImageService;
import isands.example.janitor.service.JanitorService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/janitor")
public class JanitorController {
    private final JanitorService janitorService;
    private final JanitorDao janitorDao;
    private final JanitorImageService janitorImageService;

    @GetMapping("/all")
    public String getJanitorList(Model model, HttpSession httpSession){
        httpSession.setAttribute("count", janitorService.count().toString());
//        httpSession.setAttribute("countPlaying", janitorService.countPlaying());
        model.addAttribute("janitors", janitorService.addListForMainPage());
        return "janitor/janitor-list";
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('janitor.create', 'janitor.update', 'janitor.read')")
    public String showForm(Model model, @RequestParam(name = "id", required = false) Long id) {
        Janitor janitor;
        if (id != null) {
            janitor = janitorService.findById(id);
        } else {
            janitor = new Janitor();
        }
        List<Long> imagesId = new ArrayList<>(janitorImageService.uploadMultipleFiles(id));
        model.addAttribute("janitorImagesId", imagesId);
        model.addAttribute("janitor", janitor);
        return "janitor/janitor-form";
    }

    @GetMapping("/{janitorId}")
    @PreAuthorize("hasAnyAuthority('janitor.read') || isAnonymous()")
    public String showInfo(Model model, @PathVariable(name = "janitorId") Long id) {
        Janitor janitor;
        if (id != null) {
            janitor = janitorService.findById(id);
        } else {
            return "redirect:/janitor/all";
        }
        List<Long> imagesId = new ArrayList<>(janitorImageService.uploadMultipleFiles(id));
        model.addAttribute("janitorImagesId", imagesId);
        model.addAttribute("janitor", janitor);
        return "janitor/janitor-info";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('janitor.create', 'janitor.update', 'janitor.read')")
    public String saveJanitor(@Valid Janitor janitor, @RequestParam("files") MultipartFile[] files,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return "janitor/janitor-form";
        }
        janitorService.save(janitor);
        uploadMultipleFiles(files, janitorDao.findById(janitor.getId()).get().getId());

        return "redirect:/janitor/all";
    }
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('janitor.delete')")
    public String deleteById(@PathVariable(name = "id") Long id) {
        janitorService.deleteById(id);
        return "redirect:/janitor/all";
    }

    @GetMapping("/status_delete/{id}")
    @PreAuthorize("hasAnyAuthority('janitor.delete')")
    public String statusDeleteById(@PathVariable(name = "id") Long id) {
        janitorService.statusDelete(id);
        return "redirect:/janitor/all";
    }

    @GetMapping("/image_delete/{id}")
    @PreAuthorize("!isAnonymous()")
    public String imageDeleteById(@PathVariable(name = "id") Long id, Model model) {
        Long janitorIdByImageId = janitorImageService.getJanitorIdByImageId(id);
        Janitor janitor = janitorService.findById(janitorIdByImageId);
        model.addAttribute("janitor", janitor);
        janitorImageService.deleteImage(id);
        if (janitorImageService.countImagesOfJanitor(janitorIdByImageId) == 0){
            janitorImageService.addStartImage(janitor);
        }
        model.addAttribute("janitorImagesId", janitorImageService.uploadMultipleFiles(janitorIdByImageId));
        return "janitor/janitor-form";
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
    @PreAuthorize("hasAnyAuthority('janitor.read') || isAnonymous()")
    @GetMapping(value = "/images/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getAllImage(@PathVariable Long id) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(janitorImageService.loadFileAsImageByIdImage(id), "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[]{};
    }

    public void uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, Long id) {
        Arrays.stream(files)
                .map(file -> janitorImageService.saveJanitorImage(id, file))
                .collect(Collectors.toList());
    }

}




