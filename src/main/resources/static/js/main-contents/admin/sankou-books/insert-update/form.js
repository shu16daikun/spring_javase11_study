import { CONST } from "./const.js";

export class SankouBooksInsertUpdateForm {
	constructor($rootForm) {
		this.$form = $rootForm || $(CONST.SELECTOR.FORM);
		this.$name = this.$form.find(CONST.SELECTOR.NAME);
		this.$colorSelect = this.$form.find(CONST.SELECTOR.COLOR_SELECT);
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();

		// A11y: 入力とエラーを関連付け
		const $invalidName = this.$name.closest(CONST.SELECTOR.CONTAINER).find(CONST.SELECTOR.INVALID);
		if ($invalidName.length) {
			const id = $invalidName.attr("id") || "name-error";
			$invalidName.attr("id", id);
			this.$name.attr("aria-describedby", id);
		}
		const $invalidColor = this.$colorSelect.closest(CONST.SELECTOR.CONTAINER).find(CONST.SELECTOR.INVALID);
		if ($invalidColor.length) {
			const id2 = $invalidColor.attr("id") || "color-error";
			$invalidColor.attr("id", id2);
			this.$colorSelect.attr("aria-describedby", id2);
		}
	}

	getValues() {
		return {
			name: (this.$name.val() || "").trim(),
			colorViewId: (this.$colorSelect.val() || "").trim(),
		};
	}

	reset() {
		this.$name.val("");
		this.$colorSelect.val("");
		this.hideErrorMessage();
		this.clearErrors();
	}

	clearErrors() {
		const $allC = this.$form.find(CONST.SELECTOR.CONTAINER);
		const $allInv = this.$form.find(CONST.SELECTOR.INVALID);

		$allC.removeClass(`${CONST.CLASS.IS_INVALID} ${CONST.CLASS.IS_VALID}`);
		$allInv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });

		this.$name.attr(CONST.ATTR.ARIA_INVALID, "false");
		this.$colorSelect.attr(CONST.ATTR.ARIA_INVALID, "false");
	}

	showErrorMessage(message) {
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();
		this.$errorAlert
			.attr(CONST.ATTR.DATA_TEXT, message)
			.text(message)
			.stop(true, true)
			.fadeIn(120);
	}

	hideErrorMessage() {
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();
		this.$errorAlert.stop(true, true).fadeOut(100, function() {
			$(this).text("").attr(CONST.ATTR.DATA_TEXT, "").hide();
		});
	}

	showNameFieldError(message) {
		const $c = this.$name.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_VALID).addClass(CONST.CLASS.IS_INVALID);
		this.$name.attr(CONST.ATTR.ARIA_INVALID, "true");
		$inv.attr(CONST.ATTR.DATA_TEXT, message).text(String(message)).css({ visibility: "visible" });
	}

	showNameFieldValid() {
		const $c = this.$name.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_INVALID).addClass(CONST.CLASS.IS_VALID);
		this.$name.attr(CONST.ATTR.ARIA_INVALID, "false");
		$inv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });
	}

	showColorFieldError(message) {
		const $c = this.$colorSelect.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_VALID).addClass(CONST.CLASS.IS_INVALID);
		this.$colorSelect.attr(CONST.ATTR.ARIA_INVALID, "true");
		$inv.attr(CONST.ATTR.DATA_TEXT, message).text(String(message)).css({ visibility: "visible" });
	}

	showColorFieldValid() {
		const $c = this.$colorSelect.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_INVALID).addClass(CONST.CLASS.IS_VALID);
		this.$colorSelect.attr(CONST.ATTR.ARIA_INVALID, "false");
		$inv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });
	}

	focusFirstError(errors) {
		if (errors.name) {
			this.$name.trigger("focus");
			return;
		}
		if (errors.colorViewId) {
			this.$colorSelect.trigger("focus");
		}
	}
}
