package com.example.chatprojekt3semester2024.UIController;

import com.example.chatprojekt3semester2024.ChatServer.ChatClient;
import com.example.chatprojekt3semester2024.model.User;
import com.example.chatprojekt3semester2024.service.UserUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Korrekt import af Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
            session.setAttribute("currentUser", authenticatedUser);

            // Start klientforbindelse til chatserveren
            new Thread(() -> {
                // Sæt serverens IP-adresse her
                String serverIpAddress = "192.168.0.100"; // Udskift med serverens faktiske IP-adresse
                ChatClient client = new ChatClient(serverIpAddress, 1234, authenticatedUser.getUsername());
                // Ingen client.start() nødvendig, da klienten starter automatisk i konstruktøren
            }).start();

            return "redirect:/chat-room";
        } else {
            model.addAttribute("error", "Ugyldig email eller kodeord");
            return "login";
        }
    }

    @GetMapping("/createUser")
    public String saveUserForm(Model model) {
        model.addAttribute("user", new User());
        return "createUser";
    }

    @PostMapping("/createUser")
    public String createUser(@ModelAttribute User user) {
        userUsecase.createUser(user);
        return "login";
    }

    @GetMapping("/menu")
    public String showMenu() {
        return "menu";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userUsecase.deleteUser(id);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @GetMapping("/chat-room")
    public String chatRoom(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("username", currentUser.getUsername());
            return "chat-room"; // Returnér HTML-siden til chatten
        } else {
            return "redirect:/login";
        }
    }
}