document.addEventListener("DOMContentLoaded", () => {
    // HTML에 숨겨둔 번역 메시지 가져오기
    const msgElement = document.getElementById("js-messages");
    const msg = msgElement ? msgElement.dataset : {};

    const usernameInput = document.getElementById("username");
    const emailInput = document.getElementById("email");
    const passwordInput = document.querySelector("input[name='password']");
    const passwordConfirmInput = document.getElementById("password-confirm");

    const usernameBtn = document.getElementById("username-check-btn");
    const emailBtn = document.getElementById("email-check-btn");

    const usernameMsg = document.getElementById("username-msg");
    const emailMsg = document.getElementById("email-msg");
    const passwordMsg = document.getElementById("password-msg");

    const submitBtn = document.getElementById("submit-btn");

    let usernameOk = false;
    let emailOk = false;
    let passwordOk = false;

    // 정규식
    const usernameRegex = /^[a-zA-Z0-9]{4,20}$/;
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!@#$%^&*()_+=-]{8,}$/;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    function updateSubmitButton() {
        submitBtn.disabled = !(usernameOk && emailOk && passwordOk);
    }
    
    /* =====================
       비밀번호 실시간 체크
       ===================== */
    function validatePassword() {
        const pw = passwordInput.value;
        const confirm = passwordConfirmInput.value;

        if (!passwordRegex.test(pw)) {
            passwordMsg.textContent = msg.pwPolicy; // 번역 적용
            passwordMsg.style.color = "red";
            passwordOk = false;
        } else if (pw !== confirm) {
            passwordMsg.textContent = msg.pwMismatch; // 번역 적용
            passwordMsg.style.color = "red";
            passwordOk = false;
        } else {
            passwordMsg.textContent = msg.pwMatch; // 번역 적용
            passwordMsg.style.color = "green";
            passwordOk = true;
        }
        updateSubmitButton();
    }

    passwordInput.addEventListener("input", validatePassword);
    passwordConfirmInput.addEventListener("input", validatePassword);

    /* ======================
       아이디 형식 (실시간)
    ====================== */
    usernameInput.addEventListener("input", () => {
        const username = usernameInput.value.trim();
        usernameOk = false;

        if (!username) {
            usernameMsg.textContent = "";
            updateSubmitButton();
            return;
        }

        if (!usernameRegex.test(username)) {
            usernameMsg.textContent = msg.idPolicy; // 번역 적용
            usernameMsg.style.color = "red";
            updateSubmitButton();
            return;
        }

        usernameMsg.textContent = msg.formatOk; // 번역 적용
        usernameMsg.style.color = "gray";
        updateSubmitButton();
    });

    /* ======================
       아이디 중복확인
    ====================== */
    usernameBtn.addEventListener("click", async () => {
        const username = usernameInput.value.trim();
        if (!usernameRegex.test(username)) {
            alert(msg.alertCheck); // 번역 적용
            return;
        }

        const res = await fetch(`/users/check-username?username=${encodeURIComponent(username)}`);
        const available = await res.json();

        if (available) {
            usernameMsg.textContent = msg.idOk; // 번역 적용
            usernameMsg.style.color = "green";
            usernameOk = true;
        } else {
            usernameMsg.textContent = msg.idDuplicate; // 번역 적용
            usernameMsg.style.color = "red";
            usernameOk = false;
        }
        updateSubmitButton();
    });

    /* ======================
       이메일 형식 (실시간)
    ====================== */
    emailInput.addEventListener("input", () => {
        const email = emailInput.value.trim();
        emailOk = false;

        if (!email) {
            emailMsg.textContent = "";
            updateSubmitButton();
            return;
        }

        if (!emailRegex.test(email)) {
            emailMsg.textContent = msg.emailInvalid; // 번역 적용
            emailMsg.style.color = "red";
            updateSubmitButton();
            return;
        }

        emailMsg.textContent = msg.formatOk; // 번역 적용
        emailMsg.style.color = "gray";
        updateSubmitButton();
    });

    /* ======================
       이메일 중복확인
    ====================== */
    emailBtn.addEventListener("click", async () => {
        const email = emailInput.value.trim();
        if (!emailRegex.test(email)) {
            alert(msg.alertCheck); // 번역 적용
            return;
        }

        const res = await fetch(`/users/check-email?email=${encodeURIComponent(email)}`);
        const available = await res.json();

        if (available) {
            emailMsg.textContent = msg.emailOk; // 번역 적용
            emailMsg.style.color = "green";
            emailOk = true;
        } else {
            emailMsg.textContent = msg.emailDuplicate; // 번역 적용
            emailMsg.style.color = "red";
            emailOk = false;
        }
        updateSubmitButton();
    });

    submitBtn.disabled = true;
});