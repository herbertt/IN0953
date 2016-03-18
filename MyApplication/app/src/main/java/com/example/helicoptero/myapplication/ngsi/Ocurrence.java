package com.example.helicoptero.myapplication.ngsi;

public class Ocurrence {

	  private Long idOcorrencia;

	  private String title;

	  private Integer occurrenceCode;

	public Long getIdOcorrencia() {
		return idOcorrencia;
	}

	public void setIdOcorrencia(Long idOcorrencia) {
		this.idOcorrencia = idOcorrencia;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setOccurenceCode(Integer occurenceCode) {
		this.occurrenceCode = occurenceCode;
	}

	public Integer getOccurenceCode() {
		return occurrenceCode;
	}
}
