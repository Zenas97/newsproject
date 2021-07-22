document.getElementById("submit_button").addEventListener("click",function(){
    var formData = getFormDataHandler();
    var requestHandler = getRequestHandler();
    formData.resetErrors();
    requestHandler.setData(formData.toJson());
    requestHandler.makeRequest();
});