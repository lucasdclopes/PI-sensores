create table medicao_sensor (
	idMedicao int not null identity(1,1),
	vlTemperatura decimal(6,2),
	vlUmidade decimal (5,2),
	dtMedicao datetime2(2) not null,
	constraint PK__medicao_sensor primary key(idMedicao desc),
	constraint UN_dtMedicao unique (dtMedicao desc)
)

create table alerta (
	idAlerta int not null identity(1,1),
	isHabilitado bit not null,
	tipoAlerta tinyint not null, --1 - TEMPERATURA. 2 - UMIDADE
	intervaloEsperaSegundos smallint not null,
	vlMax decimal(6,2) null, 
	vlMin decimal(6,2) null,
	dtCriado datetime2(2) not null,
	dtUltimoEnvio datetime2(2) null,
	destinatarios varchar(1000) not null,
	constraint PK__alerta primary key (idAlerta desc)
) 
go

create table alerta_enviado (
	idEnviado int not null identity(1,1),
	idAlerta int not null,
	dtEnvio datetime2(2) not null,
	constraint PK__alerta_enviado primary key (idEnviado desc),
	constraint FK__alerta_enviado__idAlerta__alerta foreign key (idAlerta)
		references alerta(idAlerta)
)

create table log_erros_sistema (
	idLogErros int not null identity(1,1),
	msgErro varchar(max) not null,
	dtLog datetime2(2) not null,
	stacktrace varchar(max) null,
	constraint PK__log_erros_sistema primary key (idLogErros desc)
)
