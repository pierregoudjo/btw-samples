import java.math.BigDecimal
import java.math.MathContext

/// <summary>
/// 	This is the CurrencyAmount Value Object that was mentioned by Rinat in BTW Episode 16.
/// 	Value Objects can often be the "nouns" (in contrast to message "verbs") in the sentences of our ubiquitous language.
/// 	The nouns are not actions, they are something that is part of the language.
///     It is a decimal that also has a currency type assotiated with it. It is used
/// 	for enforcing logical consistency within the billing system.
///
/// 	We use a Value Objedct here to reduce the chances of messing up by mixing different
/// 	currencies together.
/// </summary>
data class CurrencyAmount(val currency: CurrencyType=CurrencyType.None, val value: BigDecimal) {

    /// <summary>
    /// Default empty currency amount with undefined currency type
    /// </summary>
    val zero = CurrencyAmount(value= BigDecimal.valueOf(0))

    /// <summary>
    /// 	Returns a
    /// 	<see cref="System.String" />
    /// 	that represents this instance.
    /// </summary>
    /// <returns>
    /// 	A
    /// 	<see cref="System.String" />
    /// 	that represents this instance.
    /// </returns>
    override fun toString(): String {
        if (currency == CurrencyType.None) {
            return value.toString()
        }
        return "$value ${currency.toString().toUpperCase()}"
    }
}

enum class CurrencyType {
    None,
    Eur,
    Usd,
    Aud,
    Cad,
    Chf,
    Gbp,
    Jpy,
    Rub,
    Mxn,
}

fun CurrencyAmount.parse(source: String): CurrencyAmount {
    val items = source.split(' ')
    val value = BigDecimal(items[0])
    val type = when {
        items.size > 1 -> CurrencyType.valueOf(items[1])
        else -> CurrencyType.None
    }
    return CurrencyAmount(type, value)

}

// extensions

/// <summary>
/// 	Rounds the specified amount to the specified amount of decimals.
/// </summary>
/// <param name="decimals">The decimals.</param>
/// <returns>rounded instance</returns>
fun CurrencyAmount.round(decimals: Int): CurrencyAmount {
    return CurrencyAmount(this.currency, this.value.round(MathContext(decimals)))
}

/// <summary>
/// 	Converts this currency amount by applying the specified
/// 	transformation function to the amount and returning new result instance.
/// </summary>
/// <param name="conversion">The conversion.</param>
/// <returns>new result instance</returns>
fun CurrencyAmount.convert(conversion: (BigDecimal) -> BigDecimal): CurrencyAmount {
    return CurrencyAmount(this.currency, conversion(this.value))
}

/// <summary>
/// 	Implements the operator +.
/// </summary>
/// <param name="originalValue">The original value.</param>
/// <param name="amount">The amount to add.</param>
/// <returns>
/// 	The result of the operator.
/// </returns>
/// ///
/// <exception cref="InvalidOperationException">
/// 	If currency types do not match
/// </exception>
infix operator fun CurrencyAmount.plus(other: CurrencyAmount): CurrencyAmount {
    throwIfMismatch(this, other, "+")
    return CurrencyAmount(this.currency, this.value - other.value)
}

/// <summary>
/// 	Implements the operator -.
/// </summary>
/// <param name="originalValue">The original value.</param>
/// <param name="amount">
/// 	The amount to subtract.
/// </param>
/// <returns>
/// 	The result of the operator.
/// </returns>
/// <exception cref="InvalidOperationException">
/// 	If currency types do not match
/// </exception>
infix operator fun CurrencyAmount.minus(other: CurrencyAmount): CurrencyAmount {
    throwIfMismatch(this, other, "-")
    return CurrencyAmount(this.currency, this.value - other.value)
}

/// <summary>
/// 	Implements the operator -.
/// </summary>
/// <param name="originalValue">The original value.</param>
/// <returns>
/// 	The result of the operator.
/// </returns>
operator fun CurrencyAmount.unaryMinus(): CurrencyAmount {
    return CurrencyAmount(this.currency, -value)
}

/// <summary>
/// 	Implements the operator *.
/// </summary>
/// <param name="originalValue">The original value.</param>
/// <param name="value">The value.</param>
/// <returns>
/// 	The result of the operator.
/// </returns>
infix operator fun CurrencyAmount.times(multiplier: BigDecimal): CurrencyAmount {
    return CurrencyAmount(this.currency, this.value * multiplier)
}

infix operator fun CurrencyAmount.div(amount: CurrencyAmount): BigDecimal {
    throwIfMismatch(this, amount, "/")
    return this.value / amount.value
}

/// <summary>
/// 	Implements the operator /.
/// </summary>
/// <param name="originalValue">The original value.</param>
/// <param name="value">The value.</param>
/// <returns>
/// 	The result of the operator.
/// </returns>
infix operator fun CurrencyAmount.div(value: BigDecimal): CurrencyAmount {
    return CurrencyAmount(this.currency, this.value / value)
}

/// <summary>
/// 	Implements the operator &gt;.
/// </summary>
/// <param name="originalValue">The original value.</param>
/// <param name="amount">
/// 	The amount to compare with.
/// </param>
/// <returns>
/// 	The result of the operator.
/// </returns>
/// <exception cref="InvalidOperationException">
/// 	If currency types do not match
/// </exception>
/// <summary>
/// 	Implements the operator &gt;=.
/// </summary>
/// <param name="originalValue">The original value.</param>
/// <param name="amount">
/// 	The amount to compare with.
/// </param>
/// <returns>
/// 	The result of the operator.
/// </returns>
/// <exception cref="InvalidOperationException">
/// 	If currency types do not match
/// </exception>
/// <summary>
/// 	Implements the operator &lt;=.
/// </summary>
/// <param name="originalValue">The original value.</param>
/// <param name="amount">
/// 	The amount to compare with.
/// </param>
/// <returns>
/// 	The result of the operator.
/// </returns>
/// <exception cref="InvalidOperationException">
/// 	If currency types do not match
/// </exception>
/// <summary>
/// 	Implements the operator &lt;.
/// </summary>
/// <param name="originalValue">The original value.</param>
/// <param name="amount">
/// 	The amount to subtract.
/// </param>
/// <returns>
/// 	The result of the operator.
/// </returns>
/// <exception cref="InvalidOperationException">
/// 	If currency types do not match
/// </exception>

/// <summary>
/// 	Implements the operator ==.
/// </summary>
/// <param name="originalValue">The original value.</param>
/// <param name="amount">The amount.</param>
/// <returns>
/// 	The result of the operator.
/// </returns>
/// <summary>
/// 	Implements the operator !=.
/// </summary>
/// <param name="originalValue">The original value.</param>
/// <param name="amount">The amount.</param>
/// <returns>
/// 	The result of the operator.
/// </returns>
infix operator fun CurrencyAmount.compareTo(other: CurrencyAmount): Int {
    throwIfMismatch(this, other, "< > <= >=")
    return this.value.compareTo(other.value)
}

private fun throwIfMismatch(left: CurrencyAmount, right: CurrencyAmount, operation: String) {
    if (left.currency != right.currency) throw IllegalStateException("Can't apply the '${operation}' operation to mismatching currencies '${left}' and '${right}'")
}

