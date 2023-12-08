import android.os.Build
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.staff.AjouterProduit
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast
import com.example.staff.R

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class AjouterProduitTest {

    private lateinit var fragment: AjouterProduit

    @Before
    fun setUp() {
        fragment = AjouterProduit.newInstance("", "")
        startFragment(fragment)
    }

    @Test
    fun shouldShowError_whenProductNameIsEmpty() {
        val addButton: Button = fragment.requireView().findViewById(R.id.Ajouterproduitbtn)
        addButton.performClick()

        val latestToast = ShadowToast.getLatestToast()
        assertNotNull(latestToast)
        assertEquals(Toast.LENGTH_SHORT, latestToast.duration)
        assertEquals("Le nom du produit est obligatoire.", ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun shouldShowError_whenProductPriceIsEmpty() {
        val productName: EditText = fragment.requireView().findViewById(R.id.Nomproduitid)
        productName.setText("Test Product")

        val addButton: Button = fragment.requireView().findViewById(R.id.Ajouterproduitbtn)
        addButton.performClick()

        val latestToast = ShadowToast.getLatestToast()
        assertNotNull(latestToast)
        assertEquals(Toast.LENGTH_SHORT, latestToast.duration)
        assertEquals("Le prix du produit est obligatoire.", ShadowToast.getTextOfLatestToast())
    }

    // Similarly, you can continue to write tests for other validation conditions.

    private fun startFragment(fragment: AjouterProduit) {
        Robolectric.buildActivity(FragmentActivity::class.java)
            .create()
            .start()
            .resume()
            .get()
            .supportFragmentManager
            .beginTransaction()
            .add(fragment, null)
            .commitNow()
    }
}