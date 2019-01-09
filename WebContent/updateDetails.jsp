<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<h1>Update Account Details</h1>
<form action="updateAccount.mm">
<label>Enter AccountNumber</label><br>
    <input type = "text"  name = "accountNumber" readonly="readonly" value="${requestScope.accounts.bankAccount.accountNumber}"/><br><br>
    <label>Enter Your Name</label><br>
    <input type = "text"  name = "name" value="${requestScope.accounts.bankAccount.accountHolderName}"/><br><br>
    <label>Enter Your Account Balance</label><br>
    <input type = "text" name = "balance" readonly="readonly" value="${requestScope.accounts.bankAccount.accountBalance}"/><br><br>
    <label>Salaried or not</label>
    <input type = "radio" name="rd" value = "yes" ${requestScope.accounts.salary==true?checked:""}/>Yes
    <input type = "radio" name = "rd" value = "no" ${requestScope.accounts.salary==true?"":checked}/>No<br><br>
    <input type = "submit" value = "update Account"/>
    <input type = "reset" value = "Reset"/>    
</form>

</body>
</html>