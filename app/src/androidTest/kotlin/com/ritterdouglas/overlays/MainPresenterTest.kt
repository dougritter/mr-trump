package com.ritterdouglas.overlays

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.mockito.Mockito.verifyZeroInteractions

import org.junit.Assert.*
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class MainPresenterTest {

    private val PERMISSIONS_WRONG = 101

    private lateinit var presenter: MainPresenter

    private val view: MainView = mock()
    private val bitmap: Bitmap = mock()

    @Before
    fun setUp() {
        presenter = MainPresenter()
        presenter.view = view
    }

    @Test
    fun testPresenter() {
        assertNotNull(presenter.view)
    }

    @Test
    fun testStartLib() {
        presenter.startLib()

        verify(presenter.view!!.initLiveView())
        verify(presenter.view!!.showStartLibButton(false))
        verify(presenter.view!!.showDescription(false))
        verify(presenter.view!!.setupOptions())
        verify(presenter.view!!.showToolbar(false))
        verify(presenter.view!!.hideStatusBar())

        verifyZeroInteractions(presenter.view!!.showStartLibButton(true))
        verifyZeroInteractions(presenter.view!!.showDescription(true))
        verifyZeroInteractions(presenter.view!!.showToolbar(true))

    }

    @Test
    fun testImageReceived() {
        presenter.imageReceived(bitmap)

        verify(presenter.view!!.showPictureButton(false))
        verify(presenter.view!!.showResultContainer(true))
        verify(presenter.view!!.setResultImage(bitmap))
        verify(presenter.view!!.showOptionsContainer(false))

        verifyZeroInteractions(presenter.view!!.showPictureButton(true))
        verifyZeroInteractions(presenter.view!!.showResultContainer(false))
        verifyZeroInteractions(presenter.view!!.showOptionsContainer(true))
    }

    @Test
    fun testFaceRecognized() {
        presenter.faceRecognized()

        verify(presenter.view!!.showOptionsContainer(true))
        verify(presenter.view!!.showPictureButton(true))

        verifyZeroInteractions(presenter.view!!.showOptionsContainer(false))
        verifyZeroInteractions(presenter.view!!.showPictureButton(false))

    }

    @Test
    fun testHandlePermissionsResultGranted() {
        presenter.handlePermissionsResult(MainActivity.MY_PERMISSIONS_REQUEST_CAMERA,
                IntArray(PackageManager.PERMISSION_GRANTED))

        verify(presenter.view!!.handlePermissions())
        verifyZeroInteractions(presenter.view!!.onError(MainPresenter.PERMISSIONS_ERROR))

    }

    @Test
    fun testHandlePermissionsResultNotGranted() {
        presenter.handlePermissionsResult(PERMISSIONS_WRONG,
                IntArray(PackageManager.PERMISSION_DENIED))

        verifyZeroInteractions(presenter.view!!.handlePermissions())
        verify(presenter.view!!.onError(MainPresenter.PERMISSIONS_ERROR))
    }

    @Test
    fun testDestroy() {
        presenter.destroy()
        assertNull(presenter.view)
    }

    /*@Test
    @Throws(Exception::class)
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        assertEquals("com.ritterdouglas.overlays", appContext.packageName)
    }*/
}
