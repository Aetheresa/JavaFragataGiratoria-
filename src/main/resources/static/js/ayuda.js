function toggleFAQ(element) {
    const answer = element.nextElementSibling;
    const chevron = element.querySelector('.chevron');
    element.classList.toggle('active');
    answer.classList.toggle('active');
    chevron.classList.toggle('rotated');
}

function scrollToSection(sectionId) {
    const element = document.getElementById(sectionId);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth' });
        const question = element.querySelector('.faq-question');
        if (question && !question.classList.contains('active')) {
            toggleFAQ(question);
        }
    }
}

document.getElementById('searchHelp').addEventListener('input', function(e) {
    const term = e.target.value.toLowerCase();
    document.querySelectorAll('.faq-item').forEach(item => {
        const q = item.querySelector('.faq-question').textContent.toLowerCase();
        const a = item.querySelector('.faq-answer').textContent.toLowerCase();
        item.style.display = (q.includes(term) || a.includes(term)) ? 'block' : 'none';
    });
});

function openChat() { alert('Iniciando chat en vivo...'); }
function openEmail() { alert('Abriendo email...'); }
function openDocs() { alert('Abriendo documentación...'); }
x