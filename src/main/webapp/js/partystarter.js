function startParty() {
    $.ajax({
        type: 'POST',
        url: "/party",
        data: "",
        success: goToParty
    })
    function goToParty(party) {
        document.location = "party.html?" + party.id
    }
}