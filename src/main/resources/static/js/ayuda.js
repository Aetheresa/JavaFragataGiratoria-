 function toggleFAQ(element) {
        const answer = element.nextElementSibling;
        const chevron = element.querySelector('.chevron');
        element.classList.toggle('active');
        answer.classList.toggle('active');
        chevron.classList.toggle('rotated');
    }

    function scrollToSection(id) {
        const el = document.getElementById(id);
        el.scrollIntoView({ behavior: 'smooth' });

        const q = el.querySelector('.faq-question');
        if (!q.classList.contains('active')) toggleFAQ(q);
    }

    document.getElementById('searchHelp').addEventListener('input', e => {
        const term = e.target.value.toLowerCase();
        document.querySelectorAll('.faq-item').forEach(item => {
            const text = item.innerText.toLowerCase();
            item.style.display = text.includes(term) ? "block" : "none";
        });
    });

    function openChat() { alert("Iniciando chat..."); }
    function openEmail() { alert("Abriendo email..."); }
    function openDocs() { alert("Abriendo documentación..."); }