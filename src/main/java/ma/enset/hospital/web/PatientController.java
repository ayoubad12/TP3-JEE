package ma.enset.hospital.web;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.enset.hospital.entities.Patient;
import ma.enset.hospital.repositories.PatientRepository;
import org.springframework.boot.Banner;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {
    private PatientRepository patientRepository;

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(name="page", defaultValue="0") int page ,
                        @RequestParam(name="size", defaultValue="4") int size,
                        @RequestParam(name = "keyword", defaultValue = "") String kw
    ){
        Page<Patient> pagePatients = patientRepository.findByNomContains(kw, PageRequest.of(page, size));
        model.addAttribute("listPatients", pagePatients.getContent());
        model.addAttribute("pages", new int[pagePatients.getTotalPages()] );
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", kw);
        return "patients";
    }
    @GetMapping("/delete")
    public String delete(Long id,
                         @RequestParam(name="keyword", defaultValue = "") String kw,
                         @RequestParam(name="page", defaultValue = "0") int page){
        patientRepository.deleteById(id);
        return "redirect:/index?page="+page+"&keyword="+kw ;
    }
    @GetMapping("/")
    public String home(){
        return "redirect:/index";
    }
    @GetMapping("/patients")
    @ResponseBody
    public List<Patient> listPatients(){
        return patientRepository.findAll();
    }

    @GetMapping("/formPatients")
    public String formPatient(Model model){
        model.addAttribute("patient", new Patient());
        return "formPatients" ;
    }

    @PostMapping("/save")
    public String save(Model model,
                       @Valid Patient patient,
                       BindingResult bindingResult,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String keyword){ // dire a spring mvc de faire la validation chaque fois qu'on essaie de stocker un patient. s'il y'a un erreur on doit l'enregistrer dans binding results
        if (bindingResult.hasErrors()) return "formPatients";
        patientRepository.save(patient);
        return "redirect:/index?page="+page+"&keyword="+keyword;
    }
    @GetMapping("/editPatient")
    public String editPatient(Model model, Long id, String keyword, int page){
        Patient patient=patientRepository.findById(id).orElse(null);
        if(patient==null) throw new RuntimeException("Patient introuvable");
        model.addAttribute("patient", patient);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        return "editPatient";
    }
}