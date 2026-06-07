package com.studyapp.controller;

import com.studyapp.model.User;
import com.studyapp.service.PasswordResetService;
import com.studyapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    // ── 로그인 ──────────────────────────────────────
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String social_error,
                            Model model) {
        if (error != null)        model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        if (logout != null)       model.addAttribute("message", "로그아웃 되었습니다.");
        if (social_error != null) model.addAttribute("error", "소셜 로그인 중 오류가 발생했습니다. 다시 시도해주세요.");
        return "auth/login";
    }

    // ── 회원가입 ─────────────────────────────────────
    @GetMapping("/register")
    public String registerPage() { return "auth/register"; }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String name,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           RedirectAttributes ra) {
        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/auth/register";
        }
        try {
            userService.register(username, email, name, password);
            ra.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/register";
        }
    }

    // ── 비밀번호 찾기 (이메일 입력) ──────────────────
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() { return "auth/forgot-password"; }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, RedirectAttributes ra) {
        // 보안상 이메일 존재 여부와 무관하게 동일 메시지 반환
        passwordResetService.sendResetEmail(email);
        ra.addFlashAttribute("message",
            "입력하신 이메일 주소로 비밀번호 재설정 링크를 발송했습니다. 메일함을 확인해주세요. (1시간 유효)");
        return "redirect:/auth/forgot-password";
    }

    // ── 비밀번호 재설정 (토큰 링크 클릭) ────────────
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        User user = passwordResetService.validateToken(token);
        if (user == null) {
            model.addAttribute("error", "유효하지 않거나 만료된 링크입니다. 비밀번호 찾기를 다시 시도해주세요.");
            return "auth/reset-password-invalid";
        }
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                RedirectAttributes ra) {
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/auth/reset-password?token=" + token;
        }
        if (newPassword.length() < 8) {
            ra.addFlashAttribute("error", "비밀번호는 8자 이상이어야 합니다.");
            return "redirect:/auth/reset-password?token=" + token;
        }
        try {
            passwordResetService.resetPassword(token, newPassword);
            ra.addFlashAttribute("message", "비밀번호가 변경되었습니다. 새 비밀번호로 로그인해주세요.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/login";
        }
    }
}
