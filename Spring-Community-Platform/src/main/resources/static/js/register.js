document.addEventListener("DOMContentLoaded", () => {

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
    const passwordRegex =
        /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!@#$%^&*()_+=-]{8,}$/;
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
            passwordMsg.textContent =
                "비밀번호는 8자 이상, 영문 + 숫자를 포함해야 합니다.";
            passwordMsg.style.color = "red";
            passwordOk = false;
        } else if (pw !== confirm) {
            passwordMsg.textContent = "비밀번호가 일치하지 않습니다.";
            passwordMsg.style.color = "red";
            passwordOk = false;
        } else {
            passwordMsg.textContent = "비밀번호가 일치합니다.";
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
            usernameMsg.textContent = "아이디는 4~20자의 영문/숫자만 가능합니다.";
            usernameMsg.style.color = "red";
            updateSubmitButton();
            return;
        }

        usernameMsg.textContent = "형식은 올바릅니다. 중복확인을 해주세요.";
        usernameMsg.style.color = "gray";
        updateSubmitButton();
    });

    /* ======================
       아이디 중복확인
    ====================== */
    usernameBtn.addEventListener("click", async () => {
        const username = usernameInput.value.trim();
        if (!usernameRegex.test(username)) {
            alert("아이디 형식을 먼저 확인하세요.");
            return;
        }

        const res = await fetch(`/users/check-username?username=${encodeURIComponent(username)}`);
        const available = await res.json();

        if (available) {
            usernameMsg.textContent = "사용 가능한 아이디입니다.";
            usernameMsg.style.color = "green";
            usernameOk = true;
        } else {
            usernameMsg.textContent = "이미 사용 중인 아이디입니다.";
            usernameMsg.style.color = "red";
            usernameOk = false;
        }
        updateSubmitButton();
    });

    /* ======================
       비밀번호 정책 (실시간)
    ====================== */
    passwordInput.addEventListener("input", () => {
        const password = passwordInput.value;

        if (!password) {
            passwordMsg.textContent = "";
            passwordOk = false;
            updateSubmitButton();
            return;
        }

        if (!passwordRegex.test(password)) {
            passwordMsg.textContent =
                "비밀번호는 8자 이상, 영문과 숫자를 포함해야 합니다.";
            passwordMsg.style.color = "red";
            passwordOk = false;
        } else {
            passwordMsg.textContent = "사용 가능한 비밀번호입니다.";
            passwordMsg.style.color = "green";
            passwordOk = true;
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
            emailMsg.textContent = "이메일 형식이 올바르지 않습니다.";
            emailMsg.style.color = "red";
            updateSubmitButton();
            return;
        }

        emailMsg.textContent = "형식은 올바릅니다. 중복확인을 해주세요.";
        emailMsg.style.color = "gray";
        updateSubmitButton();
    });

    /* ======================
       이메일 중복확인
    ====================== */
    emailBtn.addEventListener("click", async () => {
        const email = emailInput.value.trim();
        if (!emailRegex.test(email)) {
            alert("이메일 형식을 먼저 확인하세요.");
            return;
        }

        const res = await fetch(`/users/check-email?email=${encodeURIComponent(email)}`);
        const available = await res.json();

        if (available) {
            emailMsg.textContent = "사용 가능한 이메일입니다.";
            emailMsg.style.color = "green";
            emailOk = true;
        } else {
            emailMsg.textContent = "이미 등록된 이메일입니다.";
            emailMsg.style.color = "red";
            emailOk = false;
        }
        updateSubmitButton();
    });

    submitBtn.disabled = true;
});
