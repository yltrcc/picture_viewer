<?php
/**
 * Created by PhpStorm.
 * User: 鑫
 * Date: 2017/6/28
 * Time: 14:10
 */

//链接数据库
$conn = mysql_connect("localhost", "root", "") or die("数据库连接错误" . mysql_error());
mysql_select_db("photograph", $conn) or die("数据库访问错误" . mysql_error());
mysql_query("SET NAMES UTF8");

//接收数据
//$userid='1';
//$nickname='易学习';

$userid = @$_GET['userid'];
$nickname = @$_GET['nickname'];
$password = @$_GET['passwd'];
$mobile = @$_GET['mobile'];

//判断修改
if ($userid) {
    if ($nickname) {
        $sql = "update user set nickname='$nickname' where id='$userid'";
    } elseif ($password) {
        $sql = "update user set password='$password' where id='$userid'";
    } elseif ($mobile) {
        $sql = "update user set mobile='$mobile' where id='$userid'";
    }

    if ($result = mysql_query($sql)) {
        $arr = array(
            'flat' => 'success',
            'message' => '修改资料成功',
        );
    } else {
        $arr = array(
            'flat' => 'fail',
            'message' => '修改资料失败',
            'id' => $userid
        );
    }
} else {
    $arr = array(
        'flat' => 'fail',
        'message' => '没有id'
    );
}
foreach ($arr as $key => $value) {
    $arr[$key] = urlencode($value);
}
//返给客户端 JSON 数据
echo urldecode(json_encode($arr));

?>