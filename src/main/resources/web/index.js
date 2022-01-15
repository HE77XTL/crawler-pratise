

function onSearch() {
    getData()
}

function getData(type) {

    function success(data) {
        console.log(data);
    }

    // 101.43.38.193
    // /apis/
    const url = `/_search?q=content:2020`
    simpleAjax({
        method:'GET',
        url: url,
        success:success,
    })
}

function simpleAjax(options) {
    const request = new XMLHttpRequest()
    request.open(options.method, options.url, true);
    request.onreadystatechange = function () {
        if(request.readyState === 4 && request.status === 200) {
            options.success && options.success(request)
        }};
    request.send();
}
