<!DOCTYPE html>
<html>
    <head>
        <title>Hello</title>
    </head>
    <body>
        <div id="myQrcode"></div>
        <script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.5.1/jquery.min.js"></script>
        <script src="https://cdn.bootcdn.net/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
        <script type="text/javascript">
            new QRCode(
                document.getElementById("myQrcode"),
                "${CodeUrl}"
            )
            $(function () {
                setInterval(function () {
                    $.ajax({
                        url: "/pay/queryOrderByNo",
                        data: {
                            orderNo: $("#orderNo").text()
                        },
                        success: function (result) {
                            if (result != null && result.platformStatus === "SUCCESS"){
                                // 这里要获取标签的内容需要使用text()而不是text属性
                                location.href = $("#returnUrl").text(); // 成功支付后跳转到指定回调函数
                            }
                        },
                        error: function (result) {
                            alert(result);
                        }

                    });
                },3000) // 每三秒定时发送一次请求
            })
        </script>

        <div id="orderNo">${OrderNo}</div>
        <div id="returnUrl">${ReturnUrl}</div>
    </body>
</html>
