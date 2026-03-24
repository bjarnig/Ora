// rankPower - "Drift" a2-nsane VAR C, d2-spatia-soft

(

~fnsane = [178.38052971582, 255.19458012749, 884.64258085768, 1748.7291446291, 9043.8864701014, 4473.3283429036, 1257.7392833936] * 0.5;
~fspatia = [137.535309, 141.977798, 216.161057, 243.542664, 258.400299, 261.461395];

Ora.addSynths;

)

(

OraPlay(Ora(~fnsane.scramble).rankPower, numChannels:8)
	.clusterGrad(0.4, 0.1, 2, 8, \drpm, 0.12, 0.25, 1.0);

)

(

OraPlay(Ora(~fnsane.scramble).rankPower(5), numChannels:8)
	.clusterGrad(0.4, 0.1, 2, 8, \drpm, 0.12, 0.25, 1.0);

)

(

OraPlay(Ora(~fspatia.scramble).rankPower(0.5), numChannels:8)
	.clusterGrad(0.0, 0.25, 1.5, 5, \drsoft, 0.03, 2.5, 0.4);

)

(

{
	OraPlay(Ora(~fnsane.scramble).rankPower, numChannels:8)
		.clusterGrad(0.4, 0.1, 2, 8, \drpm, 0.12, 0.25, 1.0);
	10.wait;
	OraPlay(Ora(~fnsane.scramble).rankPower(5), numChannels:8)
		.clusterGrad(0.4, 0.1, 2, 8, \drpm, 0.12, 0.25, 1.0);
	10.wait;
	OraPlay(Ora(~fnsane.scramble).rankPower(20), numChannels:8)
		.clusterGrad(0.4, 0.1, 2, 8, \drpm, 0.12, 0.25, 1.0);
}.fork

)