// posts-detail.js

const postRoot = document.getElementById("post-root");
const postId = postRoot?.dataset.postId;

if (!postId) {
    console.error("❌ postId 없음 - data-post-id 확인");
}

/* =======================
   좋아요 관련
======================= */
const likeBtn = document.getElementById("like-btn");
const likeCountSpan = document.getElementById("like-count");

/* =======================
   댓글 페이징 상태
======================= */
let currentCommentPage = 0;

/* =======================
   댓글 로드 (페이징)
======================= */
function loadComments(page = 0) {
    currentCommentPage = page;

    fetch(`/api/posts/${postId}/comments?page=${page}`)
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById("comment-list");
            list.innerHTML = "";

            data.content.forEach(comment => {
                const div = document.createElement("div");

                let actionBtns = "";
                if (comment.mine) {
                    actionBtns = `
                        <div class="d-flex gap-1">
                            <button class="btn btn-sm btn-outline-primary edit-btn"
                                    data-id="${comment.id}">
                                수정
                            </button>
                            <button class="btn btn-sm btn-outline-danger delete-btn"
                                    data-id="${comment.id}">
                                삭제
                            </button>
                        </div>
                    `;
                }

                div.innerHTML = `
                    <div class="card mb-2">
                        <div class="card-body py-2">

                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <strong>${comment.username}</strong>
                                    <span class="text-muted small ms-2">
                                        ${comment.createdAt}
                                    </span>
                                </div>
                                ${actionBtns}
                            </div>

                            <div class="mt-2 comment-content"
                                 data-id="${comment.id}">
                                ${comment.content}
                            </div>

                        </div>
                    </div>
                `;

                list.appendChild(div);
            });

            renderCommentPagination(data);
        });
}

/* =======================
   댓글 페이징 UI
======================= */
function renderCommentPagination(pageData) {
    const container = document.getElementById("comment-pagination");
    container.innerHTML = "";

    if (pageData.totalPages <= 1) return;

    const ul = document.createElement("ul");
    ul.className = "pagination pagination-sm justify-content-center";

    // 이전
    const prev = document.createElement("li");
    prev.className = `page-item ${pageData.first ? "disabled" : ""}`;
    prev.innerHTML = `<a class="page-link" href="#">이전</a>`;
    prev.onclick = () => {
        if (!pageData.first) {
            loadComments(pageData.number - 1);
        }
    };
    ul.appendChild(prev);

    // 페이지 번호
    for (let i = 0; i < pageData.totalPages; i++) {
        const li = document.createElement("li");
        li.className = `page-item ${i === pageData.number ? "active" : ""}`;
        li.innerHTML = `<a class="page-link" href="#">${i + 1}</a>`;
        li.onclick = () => loadComments(i);
        ul.appendChild(li);
    }

    // 다음
    const next = document.createElement("li");
    next.className = `page-item ${pageData.last ? "disabled" : ""}`;
    next.innerHTML = `<a class="page-link" href="#">다음</a>`;
    next.onclick = () => {
        if (!pageData.last) {
            loadComments(pageData.number + 1);
        }
    };
    ul.appendChild(next);

    container.appendChild(ul);
}

/* =======================
   댓글 작성
======================= */
function addComment(e) {
    e.preventDefault();

    const textarea = document.getElementById("comment-content");
    const content = textarea.value.trim();

    if (!content) return;

    fetch(`/api/posts/${postId}/comments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ content })
    }).then(res => {
        if (res.ok) {
            textarea.value = "";
            loadComments(currentCommentPage);
        }
    });
}

/* =======================
   댓글 이벤트 위임
======================= */
document.addEventListener("click", e => {

    // 수정
    if (e.target.classList.contains("edit-btn")) {
        const id = e.target.dataset.id;
        const contentDiv = document.querySelector(
            `.comment-content[data-id="${id}"]`
        );

        const oldContent = contentDiv.textContent.trim();

        contentDiv.innerHTML = `
            <textarea class="form-control mb-2 edit-textarea"
                      rows="3">${oldContent}</textarea>
            <div class="text-end">
                <button class="btn btn-sm btn-primary save-btn"
                        data-id="${id}">
                    저장
                </button>
                <button class="btn btn-sm btn-secondary cancel-btn"
                        data-content="${oldContent}">
                    취소
                </button>
            </div>
        `;
    }

    // 저장
    if (e.target.classList.contains("save-btn")) {
        const id = e.target.dataset.id;
        const contentDiv = e.target.closest(".comment-content");
        const textarea = contentDiv.querySelector(".edit-textarea");
        const newContent = textarea.value.trim();

        if (!newContent) {
            alert("내용을 입력하세요");
            return;
        }

        fetch(`/api/comments/${id}`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ content: newContent })
        }).then(res => {
            if (res.ok) loadComments(currentCommentPage);
            else alert("수정 권한 없음");
        });
    }

    // 취소
    if (e.target.classList.contains("cancel-btn")) {
        const oldContent = e.target.dataset.content;
        const contentDiv = e.target.closest(".comment-content");
        contentDiv.textContent = oldContent;
    }

    // 삭제
    if (e.target.classList.contains("delete-btn")) {
        const id = e.target.dataset.id;
        if (!confirm("댓글을 삭제하시겠습니까?")) return;

        fetch(`/api/comments/${id}`, { method: "DELETE" })
            .then(res => {
                if (res.ok) loadComments(currentCommentPage);
                else alert("삭제 권한 없음");
            });
    }
});

/* =======================
   좋아요
======================= */
likeBtn?.addEventListener("click", () => {
    fetch(`/posts/${postId}/like`, { method: "POST" })
        .then(res => res.json())
        .then(data => {
            likeBtn.classList.toggle("btn-danger", data.liked);
            likeBtn.classList.toggle("btn-outline-danger", !data.liked);
            likeCountSpan.textContent = data.likeCount;
        });
});

/* =======================
   초기 실행
======================= */
if (postId) {
    document
        .getElementById("comment-form")
        ?.addEventListener("submit", addComment);

    loadComments();
}
