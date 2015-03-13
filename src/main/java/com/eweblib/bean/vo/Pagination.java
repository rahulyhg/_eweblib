package com.eweblib.bean.vo;

public class Pagination {

	private int rows;

	private int page;

	private int total;

	private int totalPages;

	public Pagination() {

	}

	public Pagination(int rows, int page) {

		this.rows = rows;
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalPages() {

		if (this.getTotal() == 0) {
			this.totalPages = 0;
		} else {

			if (this.getRows() > 0) {
				float pages = (float) this.getTotal() / this.getRows();
				int temp = (int) pages;
				if (pages > temp) {
					temp++;
				}
				this.totalPages = temp;
			} else {
				this.totalPages = 1;
			}
		}

		return totalPages;
	}

}
