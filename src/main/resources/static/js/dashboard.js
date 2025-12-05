    document.addEventListener("DOMContentLoaded", function() {
        const searchInput = document.getElementById("search-input");
        const navButtons = document.querySelectorAll(".nav-button");

        searchInput.addEventListener("input", function() {
            const searchTerm = searchInput.value.toLowerCase();

            navButtons.forEach(button => {
                const buttonText = button.textContent.toLowerCase();

                if (buttonText.includes(searchTerm)) {
                    button.style.backgroundColor = "#ff8c00";
                } else {
                    button.style.backgroundColor = "";
                }
            });
        });
    });