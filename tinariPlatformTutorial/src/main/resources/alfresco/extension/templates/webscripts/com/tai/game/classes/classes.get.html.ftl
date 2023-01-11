<html>
	<body>
		<h1>CLASSES</h1>
		<#list classes?values as class>
			<div>
				<strong>Type: </strong><span>${class["g:classType"]}</span>
			</div>
			<#if class_has_next>
				<hr color="black">
			</#if>
		</#list>
	</body>
</html>