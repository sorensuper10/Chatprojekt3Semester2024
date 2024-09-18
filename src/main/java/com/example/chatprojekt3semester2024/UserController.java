package com.example.chatprojekt3semester2024;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Korrekt import af Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserUsecase userUsecase;

    @GetMapping("/")
    public String homePage() {
        return "login";
    }

    @PostMapping("/login")
    public String findLogin(@ModelAttribute User user, Model model, HttpSession session) {
        User authenticatedUser = userUsecase.findLogin(user.getUsername(), user.getPassword());
        if (authenticatedUser != null) {
            // Gem brugeren i sessionen
            session.setAttribute("currentUser", authenticatedUser);
            return "redirect:/menu";  // Redirecter til menu-siden
        } else {
            // Tilføj en fejlbesked, der vises på login-siden
            model.addAttribute("error", "Ugyldig email eller kodeord");
            return "login";  // Bliver på login-siden med fejlmeddelelse
        }
    }

    @GetMapping("/menu")
    public String showMenu() {
        return "menu";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }
}
