package com.example.chatprojekt3semester2024.UIController;

import com.example.chatprojekt3semester2024.ChatServer.ChatClient;
import com.example.chatprojekt3semester2024.model.User;
import com.example.chatprojekt3semester2024.service.UserUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

            // Opret en ny tråd til klientforbindelsen til chatserveren
            new Thread(() -> {
                ChatClient client = new ChatClient("localhost", 8081, authenticatedUser.getUsername());
                // Ingen client.start() nødvendig, da klienten allerede starter i konstruktøren
            }).start();

            return "redirect:/menu";  // Redirecter til menu-siden
        } else {
            model.addAttribute("error", "Ugyldig email eller kodeord");
            return "login";
        }
    }

    @GetMapping("/Chat")
    public String chat() {
        return "chat-room";
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
    public String showMenu(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        return "menu";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userUsecase.deleteUser(id);
        return "redirect:/";
    }

    @GetMapping("/editUser")
    public String editUserForm(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("user", currentUser);
            return "editUser"; // Returner korrekt Thymeleaf-skabelon
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/user/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        userUsecase.findUserByID(id).ifPresent(user -> model.addAttribute("user", user));
        return "editUser";
    }

    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute User user, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        userUsecase.updateUser(user);  // Opdater brugeren i databasen
        session.setAttribute("currentUser", user);  // Opdater sessionen med den opdaterede bruger
        return "redirect:/menu";  // Redirect til menu-siden efter opdatering
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }
    @GetMapping("/chat-room")
    public String chatRoom(Model model, HttpSession session) {
        // Tjek om brugeren er logget ind
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("username", currentUser.getUsername());
            return "chat-room"; // Returnér HTML-siden til chatten
        } else {
            return "redirect:/login"; // Hvis ikke logget ind, send tilbage til login-siden
        }
    }
}